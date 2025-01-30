package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
import com.clokey.server.domain.model.repository.MemberRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.LoginException;
import java.util.Date;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.access-expiration}")  // accessToken 만료 시간
    private long accessExpirationTime;

    @Value("${jwt.refresh-expiration}") // refreshToken 만료 시간
    private long refreshExpirationTime;

    @Autowired
    private MemberRepositoryService memberRepositoryService;

    @Override
    public String generateAccessToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    @Override
    public String generateRefreshToken(Long userId) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }


    @Override
    public boolean validateJwtToken(String token) {
        try {
            Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return true;
        } catch (SignatureException | MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public String extractEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
        return claims.get("email", String.class);
    }

    @Override
    public AuthDTO.TokenResponse authenticateKakaoUser(String kakaoAccessToken){
        //카카오에서 사용자 정보 가져오기
        AuthDTO.KakaoUserResponse kakaoUser = getUserInfoFromKakao(kakaoAccessToken);

        // DB에서 해당 이메일을 가진 사용자 찾기
        Optional<Member> optionalMember = memberRepositoryService.findMemberByEmail(kakaoUser.getKakaoAccount().getEmail());

        Member member;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();  // Optional에서 Member 추출
        } else {
            // DB에 사용자 정보가 없으면 회원가입
            member = Member.builder()
                    .nickname(kakaoUser.getKakaoAccount().getProfile().getNickname())
                    .email(kakaoUser.getKakaoAccount().getEmail())
                    .registerStatus(RegisterStatus.NOT_AGREED)
                    .socialType(SocialType.KAKAO)
                    .build();
            memberRepositoryService.saveMember(member);
        }

        //어세스토큰, 리프레시토큰 생성
        String accessToken = generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = generateRefreshToken(member.getId());

        //리프레쉬 토큰을 DB에 저장
        member.setRefreshToken(refreshToken);
        memberRepositoryService.saveMember(member);

        //토큰 반환
        return new AuthDTO.TokenResponse(member.getId(), member.getEmail(), member.getNickname(), accessToken, refreshToken, member.getRegisterStatus());
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);




// 로그 확인용 메서드
//    public AuthDTO.KakaoUserResponse getUserInfoFromKakao(String accessToken) {
//        RestTemplate restTemplate = new RestTemplate();
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(accessToken);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        String url = "https://kapi.kakao.com/v2/user/me"
//                + "?property_keys=[\"kakao_account.profile\", \"kakao_account.email\"]";
//
//        try {
//            ResponseEntity<String> response = restTemplate.exchange(
//                    url,
//                    HttpMethod.GET,
//                    entity,
//                    String.class
//            );
//
//            logger.info("Kakao API Response: {}", response.getBody());
//
//            ObjectMapper objectMapper = new ObjectMapper();
//            AuthDTO.KakaoUserResponse kakaoUser = objectMapper.readValue(response.getBody(), AuthDTO.KakaoUserResponse.class);
//
//            // ✅ 예외 처리: profile도 없으면 문제!
//            if (kakaoUser.getKakaoAccount() == null || kakaoUser.getKakaoAccount().getProfile() == null) {
//                throw new MemberException(ErrorStatus.ESSENTIAL_TERM_NOT_AGREED);
//            }
//
//            return kakaoUser;
//
//        } catch (HttpClientErrorException e) {
//            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//                throw new MemberException(ErrorStatus.INVALID_TOKEN);
//            }
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 요청에 실패했습니다.");
//        } catch (JsonProcessingException e) {
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "JSON 파싱 오류");
//        }
//    }





    // 카카오 사용자 정보 조회 메서드
    public AuthDTO.KakaoUserResponse getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<AuthDTO.KakaoUserResponse> response = restTemplate.exchange(
                    "https://kapi.kakao.com/v2/user/me",
                    HttpMethod.GET,
                    entity,
                    AuthDTO.KakaoUserResponse.class
            );

            return response.getBody();
        }

        /* 프론트와 연결 시 이 부분 주석해제 */
        catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new MemberException(ErrorStatus.INVALID_TOKEN);
            }

            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 요청에 실패했습니다.");
        }
            /* 프론트와 연결 시 이 부분 주석해제 */



            /* 프론트와 연결시 이 부분 주석*/
//        catch (HttpClientErrorException e) {
//            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
//                // ✅ 유효하지 않은 토큰이라도 더미 데이터 반환
//                AuthDTO.KakaoUserResponse dummyResponse = new AuthDTO.KakaoUserResponse();
//                dummyResponse.setId(123456L);  // 임의의 사용자 ID 설정
//
//                // KakaoAccount 객체와 Profile 설정
//                AuthDTO.KakaoUserResponse.KakaoAccount kakaoAccount = new AuthDTO.KakaoUserResponse.KakaoAccount();
//                kakaoAccount.setEmail("dummy@example.com");
//
//                AuthDTO.KakaoUserResponse.KakaoAccount.Profile profile = new AuthDTO.KakaoUserResponse.KakaoAccount.Profile();
//                profile.setNickname("Dummy User");
//
//                // Profile을 KakaoAccount에 설정
//                kakaoAccount.setProfile(profile);
//                dummyResponse.setKakaoAccount(kakaoAccount);
//
//                return dummyResponse;
//            }
//            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 요청에 실패했습니다.");
//        }

            /* 프론트와 연결 시 이 부분 주석*/

        }


}



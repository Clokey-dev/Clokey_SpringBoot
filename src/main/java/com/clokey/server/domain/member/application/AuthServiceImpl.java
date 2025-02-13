package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.MemberStatus;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import jakarta.transaction.Transactional;
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
        Member member= memberRepositoryService.findMemberById(userId);
        if(member == null){
            throw new MemberException(ErrorStatus.NO_SUCH_MEMBER);
        }
        String email = member.getEmail();

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
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



    @Transactional
    @Override
    public ResponseEntity<AuthDTO.TokenResponse> authenticateKakaoUser(String kakaoAccessToken){
        // 카카오에서 사용자 정보 가져오기
        AuthDTO.KakaoUserResponse kakaoUser = getUserInfoFromKakao(kakaoAccessToken);

        // DB에서 해당 이메일을 가진 사용자 찾기
        Optional<Member> optionalMember = memberRepositoryService.findMemberByEmail(kakaoUser.getKakaoAccount().getEmail());

        Member member;
        boolean isNewUser = false;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();  // 기존 사용자
            if (member.getKakaoId() == null||member.getKakaoId().isBlank()) {
                if(member.getStatus()== MemberStatus.INACTIVE){
                    member.updateStatus();
                    member.updateInactiveDate(null);
                    memberRepositoryService.saveMember(member);
                }
                // DB에 카카오 ID가 없으면 업데이트
                member.updateKakaoId(kakaoUser.getId());
                memberRepositoryService.saveMember(member);
            }
        } else {
            // DB에 사용자 정보가 없으면 회원가입
            member = Member.builder()
                    .kakaoId(kakaoUser.getId())
                    .nickname(kakaoUser.getKakaoAccount().getProfile().getNickname())
                    .email(kakaoUser.getKakaoAccount().getEmail())
                    .registerStatus(RegisterStatus.NOT_AGREED)
                    .socialType(SocialType.KAKAO)
                    .build();
            memberRepositoryService.saveMember(member);
            isNewUser = true; // 새로운 사용자
        }

        String accessToken = generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = generateRefreshToken(member.getId());

        member.updateToken(accessToken, refreshToken);
        memberRepositoryService.saveMember(member);

        // 토큰 반환
        AuthDTO.TokenResponse tokenResponse = new AuthDTO.TokenResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getAccessToken(),
                member.getRefreshToken(),
                member.getRegisterStatus()
        );

        // 새로운 사용자라면 201 Created 반환, 기존 사용자라면 200 OK 반환
        if (isNewUser) {
            return ResponseEntity.status(HttpStatus.CREATED).body(tokenResponse); // 201
        } else {
            return ResponseEntity.ok(tokenResponse); // 200
        }
    }



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

        catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new MemberException(ErrorStatus.INVALID_TOKEN);
            }

            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }



    }

    @Transactional
    @Override
    public AuthDTO.TokenResponse refreshAccessToken(String refreshToken) {

        if (isRefreshTokenExpired(refreshToken)) {
            throw new MemberException(ErrorStatus.EXPIRED_REFRESH_TOKEN);  // 리프레시 토큰 만료
        }

        // 리프레시 토큰 검증
        if (!validateJwtToken(refreshToken)) {
            throw new MemberException(ErrorStatus.INVALID_TOKEN);  // 유효하지 않은 리프레시 토큰
        }

        // 리프레시 토큰에서 userId 추출
        String email = extractEmailFromToken(refreshToken);

        // DB에서 사용자 정보 조회 (Member가 null일 수 있음)
        Member member = memberRepositoryService.findMemberByEmail(email).orElse(null);
        if (member == null) {
            throw new MemberException(ErrorStatus.LOGIN_FAILED);  // 사용자가 존재하지 않으면 오류
        }

        // 새로운 액세스 토큰 생성
        String newAccessToken = generateAccessToken(member.getId(), member.getEmail());

        // 새로운 리프레시 토큰 생성 (optional, 리프레시 토큰 재발급 여부)
        String newRefreshToken = generateRefreshToken(member.getId());

        // 새로운 토큰을 DB에 업데이트
        member.updateToken(newAccessToken, newRefreshToken);
        memberRepositoryService.saveMember(member);

        // 새로 발급된 토큰들 반환
        AuthDTO.TokenResponse tokenResponse = new AuthDTO.TokenResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                newAccessToken,
                newRefreshToken,
                member.getRegisterStatus()
        );

        return tokenResponse;
    }

    private boolean isRefreshTokenExpired(String refreshToken) {
        try {
            // 토큰에서 만료 시간 추출 (JWT에서 만료 시간은 "exp" 클레임에 저장)
            Date expiration = extractExpirationFromToken(refreshToken);

            // 만료 시간이 현재 시간 이전이면 만료된 것
            return expiration.before(new Date());
        } catch (Exception e) {
            // 토큰에서 만료 시간 추출에 실패하면 만료된 것으로 간주
            return true;
        }
    }

    // JWT에서 만료 시간을 추출하는 메소드
    private Date extractExpirationFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey)  // 비밀키로 서명된 토큰 파싱
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration();  // 만료 시간을 반환
    }




}



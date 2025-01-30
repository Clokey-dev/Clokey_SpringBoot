package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
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
    public ResponseEntity<AuthDTO.TokenResponse> authenticateKakaoUser(String kakaoAccessToken){
        // 카카오에서 사용자 정보 가져오기
        AuthDTO.KakaoUserResponse kakaoUser = getUserInfoFromKakao(kakaoAccessToken);

        // DB에서 해당 이메일을 가진 사용자 찾기
        Optional<Member> optionalMember = memberRepositoryService.findMemberByEmail(kakaoUser.getKakaoAccount().getEmail());

        Member member;
        boolean isNewUser = false;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();  // 기존 사용자
        } else {
            // DB에 사용자 정보가 없으면 회원가입
            member = Member.builder()
                    .nickname(kakaoUser.getKakaoAccount().getProfile().getNickname())
                    .email(kakaoUser.getKakaoAccount().getEmail())
                    .registerStatus(RegisterStatus.NOT_AGREED)
                    .socialType(SocialType.KAKAO)
                    .build();
            memberRepositoryService.saveMember(member);
            isNewUser = true; // 새로운 사용자
        }

        // 어세스토큰, 리프레시토큰 생성
        String accessToken = generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = generateRefreshToken(member.getId());

        // 리프레쉬 토큰을 DB에 저장
        member.setRefreshToken(refreshToken);
        memberRepositoryService.saveMember(member);

        // 토큰 반환
        AuthDTO.TokenResponse tokenResponse = new AuthDTO.TokenResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                accessToken,
                refreshToken,
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


}



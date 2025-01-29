package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.model.entity.Member;
import com.clokey.server.domain.model.repository.MemberRepository;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Date;

@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    @Autowired
    private MemberRepositoryService memberRepositoryService;

    @Override
    public String generateJwtToken(Long userId, String email) {
        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
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
    public AuthDTO.KakaoUserResponse getKakaoUserInfo(String accessToken) {
        // 카카오 사용자 정보 조회
        AuthDTO.KakaoUserResponse kakaoUser = getUserInfoFromKakao(accessToken);

        // 카카오 사용자 정보가 DB에 없으면 신규로 회원가입
        Member member = memberRepositoryService.findMemberByEmail(kakaoUser.getKakaoAccount().getEmail());
        if (member == null) {
            // 신규 회원 정보 DB에 저장 (Builder 패턴 사용)
            member = Member.builder()
                    .nickname(kakaoUser.getKakaoAccount().getProfile().getNickname())
                    .email(kakaoUser.getKakaoAccount().getEmail())
                    .build();
            memberRepositoryService.saveMember(member);
        }

        return kakaoUser;
    }

    // 카카오 사용자 정보 조회 메서드
    private AuthDTO.KakaoUserResponse getUserInfoFromKakao(String accessToken) {
        RestTemplate restTemplate = new RestTemplate();

        // 헤더에 Access Token 추가
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        // 카카오 API 호출
        ResponseEntity<AuthDTO.KakaoUserResponse> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",  // 카카오 사용자 정보 API
                HttpMethod.GET,
                entity,
                AuthDTO.KakaoUserResponse.class  // 반환 타입을 KakaoUserResponse로 수정
        );

        if (response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            throw new RuntimeException("카카오 사용자 정보 조회 실패");
        }
    }

}
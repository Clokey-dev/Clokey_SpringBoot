package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
import com.clokey.server.domain.search.application.SearchRepositoryService;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import javax.security.auth.login.LoginException;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SearchRepositoryService searchRepositoryService;

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

        String accessToken = generateAccessToken(member.getId(), member.getEmail());
        String refreshToken = generateRefreshToken(member.getId());

        member.updateToken(accessToken, refreshToken);
        memberRepositoryService.saveMember(member);

        // ES 동기화
        try {
            searchRepositoryService.updateMemberDataToElasticsearch(member);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }

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



    // 애플



    @Value("${apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${apple.key.id}")
    private String APPLE_LOGIN_KEY;

    @Value("${apple.client-id}")
    private String APPLE_CLIENT_ID;

    @Value("${apple.redirect-uri}")
    private String APPLE_REDIRECT_URL;

    @Value("${apple.key.path}")
    private String APPLE_KEY_PATH;

    @Value("${apple.privateKey}")
    private String privateKeyString;

    //1. 여기까지 설정값을 application.properties에서 가져옴

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";


    public String getAppleLogin() {
        return APPLE_AUTH_URL + "/auth/authorize"
                + "?client_id=" + APPLE_CLIENT_ID
                + "&redirect_uri=" + APPLE_REDIRECT_URL
                + "&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }

    //2. 여기까지 주소 가져옴


    public AuthDTO.TokenResponse appleLogin(String code, String deviceToken, String refreshToken) {
        // code가 null인 경우 처리
        if (code == null || code.isBlank()) {
            throw new MemberException(ErrorStatus.INVALID_CODE);
        }

        String clientSecret = createClientSecret();
        String userId = "";
        String email = "";
        String accessToken = "";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-type", "application/x-www-form-urlencoded");

            MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
            params.add("grant_type", "authorization_code");
            params.add("client_id", APPLE_CLIENT_ID);
            params.add("client_secret", clientSecret);
            params.add("code", code);
            params.add("redirect_uri", APPLE_REDIRECT_URL);

            RestTemplate restTemplate = new RestTemplate();
            HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

            // Apple API 호출
            ResponseEntity<String> response = restTemplate.exchange(
                    APPLE_AUTH_URL + "/auth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            // 응답 상태 코드 체크
            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new MemberException(ErrorStatus.NO_RESPONSE);
            }

            // 응답 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            // access_token 및 id_token 유효성 확인
            if (!jsonObj.containsKey("access_token") || !jsonObj.containsKey("id_token")) {
                throw new MemberException(ErrorStatus.INVALID_RESPONSE);
            }

            accessToken = String.valueOf(jsonObj.get("access_token"));

            // JWT 토큰 파싱
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(jsonObj.get("id_token")));
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            String jsonString = new ObjectMapper().writeValueAsString(claimsSet.toJSONObject());
            JSONObject payload = new JSONObject(new ObjectMapper().readValue(jsonString, Map.class));

            userId = String.valueOf(payload.get("sub"));
            email = String.valueOf(payload.get("email"));

        } catch (Exception e) {
            e.printStackTrace();
            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }

        // 회원 조회 또는 신규 등록
        Optional<Member> optionalMember = memberRepositoryService.findMemberByEmail(email);

        Member member;
        boolean isNewUser = false;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();  // 기존 사용자

                member.updateDeviceToken(deviceToken);
                memberRepositoryService.saveMember(member);

                if(member.getAppleRefreshToken()==null || !member.getAppleRefreshToken().equals(refreshToken)){
                    member.updateAppleRefreshToken(refreshToken);
                    memberRepositoryService.saveMember(member);
                }



        } else {
            member = Member.builder()
                    .email(email)
                    .socialType(SocialType.APPLE)
                    .registerStatus(RegisterStatus.NOT_AGREED)
                    .deviceToken(deviceToken)
                    .build();
            memberRepositoryService.saveMember(member);
            isNewUser = true; // 새로운 사용자
        }

        // 토큰 생성
        String jwtAccessToken = generateAccessToken(member.getId(), member.getEmail());
        String jwtRefreshToken = generateRefreshToken(member.getId());

        member.updateToken(jwtAccessToken, jwtRefreshToken);
        memberRepositoryService.saveMember(member);

        // ES 동기화
        try {
            searchRepositoryService.updateMemberDataToElasticsearch(member);
        } catch (IOException e) {
            throw new SearchException(ErrorStatus.ELASTIC_SEARCH_SYNC_FAULT);
        }

        // 응답 반환
        return new AuthDTO.TokenResponse(
                member.getId(),
                member.getEmail(),
                member.getNickname(),
                member.getAccessToken(),
                member.getRefreshToken(),
                member.getRegisterStatus()
        );
    }

    private String createClientSecret() {
        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.ES256)
                .keyID(APPLE_LOGIN_KEY)
                .build();

        Date now = new Date();

        // ✅ claimsSet을 Builder로 생성
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .issuer(APPLE_TEAM_ID)
                .issueTime(now)
                .expirationTime(new Date(now.getTime() + 3600000)) // 1시간 후 만료
                .audience(APPLE_AUTH_URL)
                .subject(APPLE_CLIENT_ID)
                .build();

        SignedJWT jwt = new SignedJWT(header, claimsSet);

        byte[] privateKeyBytes = getPrivateKey(); // 예외 처리 제거
        if (privateKeyBytes == null) {
            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKeyBytes);
        KeyFactory kf = null;
        try {
            kf = KeyFactory.getInstance("EC");
        } catch (NoSuchAlgorithmException e) {
            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }

        try {
            ECPrivateKey ecPrivateKey = (ECPrivateKey) kf.generatePrivate(spec);
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey);
            jwt.sign(jwsSigner);
        } catch (InvalidKeySpecException | JOSEException e) {
            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }

        return jwt.serialize();
    }

    //4. 여기까지 클라이언트 시크릿 생성

    public byte[] getPrivateKey() {
        if (privateKeyString == null || privateKeyString.isBlank()) {
            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }

        // "-----BEGIN PRIVATE KEY-----" 과 "-----END PRIVATE KEY-----" 제거
        String key = privateKeyString.replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("[\\r\\n]", "");  // \r, \n을 명시적으로 제거

        try {
            return Base64.getDecoder().decode(key);
        } catch (IllegalArgumentException e) {
            throw new MemberException(ErrorStatus.LOGIN_FAILED);
        }
    }

    //5. 여기까지 프라이빗 키 가져오기

}



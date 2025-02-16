package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
import com.clokey.server.domain.search.application.SearchRepositoryService;
import com.clokey.server.domain.search.exception.SearchException;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.bouncycastle.util.io.pem.PemObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Map;
import java.util.Date;
import com.nimbusds.jose.crypto.ECDSASigner;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleAuthServiceImpl implements AppleAuthService {

    private final MemberRepositoryService memberRepositoryService;

    private final AuthService authService;

    private final SearchRepositoryService searchRepositoryService;

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


    public AuthDTO.TokenResponse login(String code, String deviceToken) {
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

            if(member.getDeviceToken() == null || member.getDeviceToken().isBlank()){
                member.updateDeviceToken(deviceToken);
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
        String jwtAccessToken = authService.generateAccessToken(member.getId(), member.getEmail());
        String jwtRefreshToken = authService.generateRefreshToken(member.getId());

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

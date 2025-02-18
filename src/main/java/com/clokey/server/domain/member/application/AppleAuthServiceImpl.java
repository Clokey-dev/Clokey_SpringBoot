package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.model.entity.enums.MemberStatus;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.*;

import com.nimbusds.jose.crypto.ECDSASigner;

import java.util.stream.Collectors;
import java.net.http.HttpRequest;
import java.net.http.HttpClient;



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


   @Transactional
    public AuthDTO.TokenResponse login(String code, String deviceToken) {

        if (code == null || code.isBlank()) {
            throw new MemberException(ErrorStatus.INVALID_CODE);
        }

        String clientSecret = createClientSecret();
        String userId = "";
        String email = "";
        String accessToken = "";
        String refreshToken = "";

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

            if (!response.getStatusCode().equals(HttpStatus.OK)) {
                throw new MemberException(ErrorStatus.NO_RESPONSE);
            }

            // 응답 파싱
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            if (!jsonObj.containsKey("access_token") || !jsonObj.containsKey("id_token")) {
                throw new MemberException(ErrorStatus.INVALID_RESPONSE);
            }

            accessToken = String.valueOf(jsonObj.get("access_token"));
            refreshToken = jsonObj.containsKey("refresh_token") ? String.valueOf(jsonObj.get("refresh_token")) : "";

            // JWT 파싱
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
            member = optionalMember.get();

            if(member.getStatus()== MemberStatus.INACTIVE){
                member.updateStatus();
                member.updateInactiveDate(null);
                memberRepositoryService.saveMember(member);
            }

            if (member.getAppleRefreshToken() == null || member.getAppleRefreshToken().isBlank()) {
                member.updateAppleRefreshToken(refreshToken);
                memberRepositoryService.saveMember(member);
            }

            if(member.getDeviceToken() == null || member.getDeviceToken().isBlank()){
                member.updateDeviceToken(deviceToken);
                memberRepositoryService.saveMember(member);
            }
        } else {
            member = Member.builder()
                    .email(email)
                    .socialType(SocialType.APPLE)
                    .registerStatus(RegisterStatus.NOT_AGREED)
                    .appleRefreshToken(refreshToken)
                    .deviceToken(deviceToken)
                    .build();
            memberRepositoryService.saveMember(member);
            isNewUser = true;
        }

        // JWT 토큰 생성
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


    public String createClientSecret() {
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





    public String getRefreshToken(String clientSecret, String authCode) {
        String refreshToken = "";

        String uriStr = "https://appleid.apple.com/auth/token";

        Map<String, String> params = new HashMap<>();
        params.put("client_secret", clientSecret); // 생성한 clientSecret
        params.put("code", authCode); // 애플 로그인 시 받은 authorizationCode
        params.put("grant_type", "authorization_code");
        params.put("client_id", APPLE_CLIENT_ID); // app bundle id

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .POST(getParamsUrlEncoded(params))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // 응답을 JSON으로 파싱
            JSONParser parser = new JSONParser();
            JSONObject parseData = (JSONObject) parser.parse(getResponse.body());

            // "refresh_token"이 존재하면 값 가져오기
            if (parseData.containsKey("refresh_token")) {
                refreshToken = parseData.get("refresh_token").toString();
            } else {
                System.out.println("refresh_token 키가 응답에 없음. 응답: " + getResponse.body());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        if (refreshToken == null || refreshToken.isBlank()) {
            System.out.println("refreshToken 생성 실패");
        }
        System.out.println("refresh is this: " + refreshToken);
        return refreshToken;
    }


    public HttpRequest.BodyPublisher getParamsUrlEncoded(Map<String, String> parameters) {
        String urlEncoded = parameters.entrySet()
                .stream()
                .map(e -> e.getKey() + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .collect(Collectors.joining("&"));
        return HttpRequest.BodyPublishers.ofString(urlEncoded);
    }

}

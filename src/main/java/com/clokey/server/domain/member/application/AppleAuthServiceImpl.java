package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.SocialType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.shaded.json.JSONObject;
import com.nimbusds.jwt.SignedJWT;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.json.JSONParser;
import org.apache.tomcat.util.json.ParseException;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.security.PrivateKey;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static java.lang.String.valueOf;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleAuthServiceImpl {

    private final MemberRepositoryService memberRepositoryService;
    private final JwtService jwtService;

    @Value("${apple.team-id}")
    private String APPLE_TEAM_ID;

    @Value("${apple.login-key")
    private String APPLE_LOGIN_KEY;

    @Getter
    @Value("${apple.clinet-id")
    private String APPLE_CLIENT_ID;

    @Value("${apple.redirect-url}")
    private String APPLE_REDIRECT_URL;

    @Value("${apple.key-path")
    private String APPLE_KEY_PATH;

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";

    public String getAppleLoginUrl(){
        return APPLE_AUTH_URL + "/auth/authorize"
                +"?client_id=" + APPLE_CLIENT_ID
                +"&redirect_uri=" + APPLE_REDIRECT_URL
                +"&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }

    public Member login(String code, HttpServletResponse response){
        String id;
        String email;
        String accessToken;

        Member member;

        try{
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(generateAuthToken(code));

            accessToken = String.valueOf(jsonObj.get("access_token"));

            //ID TOKEN을 통해 회원 고유 식별자 받기
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(jsonObj.get("id_token")));
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();

            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject payload = objectMapper.readValue(getPayload.toJSONObject().toJSONString(), JSONObject.class);

            id=String.valueOf(payload.get("sub"));
            email=String.valueOf(payload.get("email"));


            Member findMember = memberRepositoryService.findMemberByEmail(email).orElse(null);

            if (findMember==null){
                //신규 회원가입의 경우 DB에 저장
                member = Member.builder()
                        .email(email)
                        .socialType(SocialType.APPLE)
                        //어세스토큰 설정
                        .build();

                memberRepositoryService.saveMember(member);
            } else{
                //기존 회원의 경우 로그인 처리
                //어세스토큰 설정후 저장
            }

            loginSuccess(member, response);
            return member;

        } catch (ParseException | JsonProcessingException e) {
            throw new RuntimeException("Failed to parse json data");
        } catch (IOException | java.text.ParseException e) {
            throw new RuntimeException(e);
        }
    }

    public void loginSuccss(Member member, HttpServletResponse response){
        String accessToken = jwtService.createAccessToken(member.getId());
        String refreshToken = jwtService.createRefreshToken(member.getId());

        jwtService.sendAccessAndRefreshToken(response, accessToken, refreshToken);
        jwtService.updateRefreshToken(user.getEmail(), refreshToken);
    }

    public String generateAuthToken(String code) throws IOException {
        if (code == null) throw new IllegalArgumentException("Failed get authorization code");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", APPLE_CLIENT_ID);
        params.add("client_secret", createClientSecretKey());
        params.add("code", code);
        params.add("redirect_uri", APPLE_REDIRECT_URL);

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<MultiValueMap<String, String>> httpEntity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    APPLE_AUTH_URL + "/auth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            return response.getBody();
        } catch (HttpClientErrorException e) {
            throw new IllegalArgumentException("Apple Auth Token Error");
        }
    }

    public String createClientSecretKey() throws IOException {
        // headerParams 적재
        Map<String, Object> headerParamsMap = new HashMap<>();
        headerParamsMap.put("kid", APPLE_LOGIN_KEY);
        headerParamsMap.put("alg", "ES256");

        // clientSecretKey 생성
        return Jwts
                .builder()
                .setHeaderParams(headerParamsMap)
                .setIssuer(APPLE_TEAM_ID)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 30)) // 만료 시간 (30초)
                .setAudience(APPLE_AUTH_URL)
                .setSubject(APPLE_CLIENT_ID)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(APPLE_KEY_PATH);
        String privateKey = new String(resource.getInputStream().readAllBytes());

        Reader pemReader = new StringReader(privateKey);
        PEMParser pemParser = new PEMParser(pemReader);
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();

        return converter.getPrivateKey(object);
    }

}

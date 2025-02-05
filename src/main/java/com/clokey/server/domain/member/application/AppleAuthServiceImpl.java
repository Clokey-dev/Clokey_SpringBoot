package com.clokey.server.domain.member.application;


import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.dto.AuthDTO;
import com.clokey.server.domain.member.exception.MemberException;
import com.clokey.server.domain.model.entity.enums.RegisterStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
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
import java.security.interfaces.ECPrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
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
    //1. 여기까지 설정값을 application.properties에서 가져옴

    private final static String APPLE_AUTH_URL = "https://appleid.apple.com";


    public String getAppleLogin() {
        return APPLE_AUTH_URL + "/auth/authorize"
                + "?client_id=" + APPLE_CLIENT_ID
                + "&redirect_uri=" + APPLE_REDIRECT_URL
                + "&response_type=code%20id_token&scope=name%20email&response_mode=form_post";
    }

    //2. 여기까지 주소 가져옴


    public AuthDTO.TokenResponse login(String code) throws Exception {


        //여기서의 code란? controller에서 프론트한테 받아온 code

        if (code == null) throw new MemberException(ErrorStatus.INVALID_CODE);

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

            ResponseEntity<String> response = restTemplate.exchange(
                    APPLE_AUTH_URL + "/auth/token",
                    HttpMethod.POST,
                    httpEntity,
                    String.class
            );

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(response.getBody());

            accessToken = String.valueOf(jsonObj.get("access_token"));

            // SignedJWT에서 Payload(Claims) 가져오기
            SignedJWT signedJWT = SignedJWT.parse(String.valueOf(jsonObj.get("id_token")));
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            // Map을 JSON 문자열로 변환 후 다시 JSONObject로 변환
            String jsonString = new ObjectMapper().writeValueAsString(claimsSet.toJSONObject());  // ✅ JSON 문자열로 변환
            JSONObject payload = new JSONObject(new ObjectMapper().readValue(jsonString, Map.class));  // ✅ JSONObject로 변환


            userId = String.valueOf(payload.get("sub"));
            email = String.valueOf(payload.get("email"));

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("API call failed", e);
        }

        Optional<Member> optionalMember = memberRepositoryService.findMemberByEmail(email);

        Member member;
        boolean isNewUser = false;
        if (optionalMember.isPresent()) {
            member = optionalMember.get();  // 기존 사용자
        } else {
            // DB에 사용자 정보가 없으면 회원가입
            member = Member.builder()
                    .email(email)
                    .socialType(SocialType.APPLE)
                    .registerStatus(RegisterStatus.NOT_AGREED)
                    .build();
            memberRepositoryService.saveMember(member);
            isNewUser = true; // 새로운 사용자
        }

        String jwtAccessToken = authService.generateAccessToken(member.getId(), member.getEmail());
        String jwtRefreshToken = authService.generateRefreshToken(member.getId());

        member.updateToken(jwtAccessToken, jwtRefreshToken);
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

        return tokenResponse;
    }

    //3. 여기까지 로그인 정보 가져옴

    private String createClientSecret() throws Exception {

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

        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(getPrivateKey());
        KeyFactory kf = KeyFactory.getInstance("EC");

        try {
            ECPrivateKey ecPrivateKey = (ECPrivateKey) kf.generatePrivate(spec);
            JWSSigner jwsSigner = new ECDSASigner(ecPrivateKey);
            jwt.sign(jwsSigner);
        } catch (JOSEException e) {
            throw new Exception("Failed to create client secret", e);
        }

        return jwt.serialize();
    }

    //4. 여기까지 클라이언트 시크릿 생성


    private byte[] getPrivateKey() throws Exception {
        byte[] content = null;
        File file = new File(APPLE_KEY_PATH);
        URL res = getClass().getResource(APPLE_KEY_PATH);

        if (res == null) {
            // 파일 시스템에서 파일을 로드할 때
            file = new File(APPLE_KEY_PATH);
        } else if ("jar".equals(res.getProtocol())) {
            // JAR 파일 내부의 리소스를 읽을 때
            try {
                InputStream input = getClass().getResourceAsStream(APPLE_KEY_PATH);
                file = File.createTempFile("tempfile", ".tmp");
                OutputStream out = new FileOutputStream(file);

                int read;
                byte[] bytes = new byte[1024];

                while ((read = input.read(bytes)) != -1) {
                    out.write(bytes, 0, read);
                }

                out.close();
                file.deleteOnExit();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        if (file.exists()) {
            try (FileReader keyReader = new FileReader(file);
                 PemReader pemReader = new PemReader(keyReader))
            {
                PemObject pemObject = pemReader.readPemObject();
                content = pemObject.getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            throw new Exception("File " + file + " not found");
        }

        return content;
    }

    public AuthDTO.TokenResponse login(String code, HttpServletResponse response) {


        return null;
    }


}

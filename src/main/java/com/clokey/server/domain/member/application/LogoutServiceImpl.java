package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.SocialType;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.net.http.HttpRequest;


@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutServiceImpl implements LogoutService {

    private final AppleAuthService appleAuthService;


    @Value("${kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    @Value("${apple.client-id}")
    private String APPLE_CLIENT_ID;

    private final MemberRepositoryService memberRepositoryService;

    @Override
    public void logout(Long userId, HttpServletRequest request) {
        // 로그아웃 처리

        if(memberRepositoryService.findMemberById(userId).getSocialType().equals("KAKAO")) {
            String kakaoId = memberRepositoryService.findMemberById(userId).getKakaoId();
            if (kakaoId != null) {
                kakaoLogout(kakaoId);
            }
        }

        // 토큰 무효화
        String result = invalidateToken(userId);

    }

    @Override
    public void unlink(Long userId) {
        Member member = memberRepositoryService.findMemberById(userId);

        if (member != null && SocialType.KAKAO == member.getSocialType()) {
            String kakaoId = member.getKakaoId();
            if (kakaoId != null) {
                kakaoUnlink(kakaoId);
            }
        } else if (member != null && SocialType.APPLE == member.getSocialType()) {
            System.out.println("🍏 애플 연동 해제 실행됨");
            String clientSecret = appleAuthService.createClientSecret();  // ✅ 새로 생성
            String refreshToken = member.getAppleRefreshToken();
            if (clientSecret != null && refreshToken != null) {
                appleUnlink(clientSecret, refreshToken);
            }
        }

        String result = invalidateToken(userId);
    }

    @Transactional
    public String invalidateToken (Long userId){

        Member member = memberRepositoryService.findMemberById(userId);

            if (member != null) {
                member.updateToken(null, null);
                memberRepositoryService.saveMember(member);
                // 토큰 무효화 처리
                return "success";
            }

            return null;
    }



        public void kakaoLogout (String kakaoId){  // String 타입의 kakaoId를 받음
            String url = "https://kapi.kakao.com/v1/user/logout";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);  // 어드민 키 입력

            try {
                Long kakaoUserId = Long.parseLong(kakaoId);  // String → Long 변환
                String body = "target_id_type=user_id&target_id=" + kakaoUserId;

                HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    System.out.println("로그아웃 성공: " + response.getBody().get("id"));
                } else {
                    System.out.println("로그아웃 실패: " + response.getStatusCode());
                }

                // 카카오 응답 전체 출력 (디버깅용)
                System.out.println("카카오 응답: " + response.getBody());

            } catch (NumberFormatException e) {
                System.out.println("로그아웃 실패: kakaoId가 올바른 숫자가 아닙니다.");
            }

        }

        public void kakaoUnlink (String kakaoId){
            String url = "https://kapi.kakao.com/v1/user/unlink";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Authorization", "KakaoAK " + KAKAO_ADMIN_KEY);

            try {
                Long kakaoUserId = Long.parseLong(kakaoId);
                String body = "target_id_type=user_id&target_id=" + kakaoUserId;

                HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);

                if (response.getStatusCode() == HttpStatus.OK) {
                    log.info("✅ 카카오 연동 해제 성공: {}", response.getBody());
                    System.out.println("✅ 해제 성공: " + response.getBody());
                } else {
                    log.warn("⚠️ 카카오 연동 해제 실패: HTTP {}", response.getStatusCode());
                    System.out.println("⚠️ 해제 실패: " + response.getStatusCode());
                }
            } catch (NumberFormatException e) {
                log.error("카카오 연동 해제 실패: kakaoId 변환 오류", e);
                System.out.println("해제 실패");
            }
        }

    public void appleUnlink (String clientSecret, String refreshToken){

        String uriStr = "https://appleid.apple.com/auth/revoke";

        Map<String, String> params = new HashMap<>();
        params.put("client_secret", clientSecret); // 생성한 client_secret
        params.put("token", refreshToken); // 생성한 refresh_token
        params.put("client_id", APPLE_CLIENT_ID); // app bundle id

        try {
            HttpRequest getRequest = HttpRequest.newBuilder()
                    .uri(new URI(uriStr))
                    .POST(appleAuthService.getParamsUrlEncoded(params))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            HttpClient httpClient = HttpClient.newHttpClient();
            HttpResponse<String> response = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());

            // 응답 상태 코드와 본문 출력
            System.out.println("응답 상태 코드: " + response.statusCode());
            System.out.println("응답 본문: " + response.body());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}

package com.clokey.server.domain.member.application;

import com.clokey.server.domain.member.domain.entity.Member;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Collections;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutServiceImpl implements LogoutService {

    @Value("${kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

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
        String result = invalidateToken(userId, request);

    }

    @Override
    public void unlink(Long userId) {
        Member member = memberRepositoryService.findMemberById(userId);

        if (member != null && "KAKAO".equals(member.getSocialType())) {
            String kakaoId = member.getKakaoId();
            if (kakaoId != null) {
                kakaoUnlink(kakaoId);
            }
        }
    }

        private String invalidateToken (Long userId, HttpServletRequest request){

            Member member = memberRepositoryService.findMemberById(userId);

            if (member != null) {
                member.updateToken(null, null);
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
                    log.info("카카오 연동 해제 성공: {}", response.getBody().get("id"));
                } else {
                    log.warn("카카오 연동 해제 실패: {}", response.getStatusCode());
                }
            } catch (NumberFormatException e) {
                log.error("카카오 연동 해제 실패: kakaoId 변환 오류", e);
            }
        }


}

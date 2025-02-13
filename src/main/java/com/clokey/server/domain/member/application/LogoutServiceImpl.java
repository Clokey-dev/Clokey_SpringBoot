package com.clokey.server.domain.member.application;

import com.clokey.server.domain.cloth.application.ClothImageRepositoryService;
import com.clokey.server.domain.cloth.application.ClothRepositoryService;
import com.clokey.server.domain.cloth.domain.entity.Cloth;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.folder.application.ClothFolderRepositoryService;
import com.clokey.server.domain.folder.application.FolderRepositoryService;
import com.clokey.server.domain.folder.domain.entity.Folder;
import com.clokey.server.domain.folder.exception.FolderException;
import com.clokey.server.domain.folder.exception.validator.FolderAccessibleValidator;
import com.clokey.server.domain.history.application.*;
import com.clokey.server.domain.history.domain.entity.Comment;
import com.clokey.server.domain.history.domain.entity.History;
import com.clokey.server.domain.history.domain.repository.CommentRepository;
import com.clokey.server.domain.history.exception.validator.HistoryAccessibleValidator;
import com.clokey.server.domain.history.exception.validator.HistoryAlreadyExistValidator;
import com.clokey.server.domain.history.exception.validator.HistoryLikedValidator;
import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.model.entity.enums.MemberStatus;
import com.clokey.server.domain.model.entity.enums.SocialType;
import com.clokey.server.domain.term.application.MemberTermRepositoryService;
import com.clokey.server.domain.term.domain.repository.MemberTermRepository;
import com.clokey.server.global.error.code.status.ErrorStatus;
import com.clokey.server.global.infra.s3.S3ImageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.net.http.HttpRequest;


@Slf4j
@RequiredArgsConstructor
@Service
public class LogoutServiceImpl implements LogoutService {

    private final AppleAuthService appleAuthService;
    private final MemberTermRepositoryService memberTermRepositoryService;

    private final FollowRepositoryService followRepositoryService;
    private final HistoryRepositoryService historyRepositoryService;
    private final CommentRepositoryService commentRepositoryService;
    private final MemberRepositoryService memberRepositoryService;
    private final MemberLikeRepositoryService memberLikeRepositoryService;
    private final HistoryImageRepositoryService historyImageRepositoryService;
    private final HashtagHistoryRepositoryService hashtagHistoryRepositoryService;
    private final ClothRepositoryService clothRepositoryService;
    private final HistoryClothRepositoryService historyClothRepositoryService;
    public final HistoryAccessibleValidator historyAccessibleValidator;
    private final ClothImageRepositoryService clothImageRepositoryService;
    private final ClothFolderRepositoryService clothFolderRepositoryService;
    private final FolderRepositoryService folderRepositoryService;
    private final FolderAccessibleValidator folderAccessibleValidator;
    private final CommentRepository commentRepository;


    @Value("${kakao.admin-key}")
    private String KAKAO_ADMIN_KEY;

    @Value("${apple.client-id}")
    private String APPLE_CLIENT_ID;


    @Transactional
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

    @Transactional
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

        if(member.getStatus()== MemberStatus.ACTIVE){
            member.updateStatus();
            member.updateInactiveDate(LocalDate.now());
            memberRepositoryService.saveMember(member);
        }

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


    @Transactional
    public void deleteData(Long memberId) {
        Member member = memberRepositoryService.findMemberById(memberId);


        if (member == null) {
            log.warn("삭제할 회원이 존재하지 않음: userId={}", memberId);
            return;
        }

        LocalDate inactiveDate = member.getInactiveDate();
        if (inactiveDate == null || inactiveDate.isAfter(LocalDate.now().minusDays(30))) {
            log.info("삭제 대상이 아님: userId={}, inactiveDate={}", memberId, inactiveDate);
            return;
        }

        // 멤버텀 삭제
        memberTermRepositoryService.deleteByMemberId(memberId);

        //알람 삭제해야됨

        //기록 삭제

        List<History> histories = memberRepositoryService.findHistoriesByMemberId(memberId);
        for (History history : histories) {
            // 내 기록 접근을 위한 검증
            historyAccessibleValidator.validateMyHistory(history.getId(), memberId);

            // 댓글 삭제
            commentRepositoryService.deleteAllComments(history.getId());

            // 기록에 관련된 옷 삭제 및 옷의 착용 수 감소
            List<Cloth> cloths = historyClothRepositoryService.findAllClothByHistoryId(history.getId());
            cloths.forEach(Cloth::decreaseWearNum);
            historyClothRepositoryService.deleteAllByHistoryId(history.getId());

            // 기록에 관련된 해시태그 삭제
            hashtagHistoryRepositoryService.deleteAllByHistoryId(history.getId());

            // 좋아요 기록 삭제
            memberLikeRepositoryService.deleteAllByHistoryId(history.getId());

            // 기록에 관련된 사진 삭제
            historyImageRepositoryService.deleteAllByHistoryId(history.getId());

            // 기록 자체 삭제
            historyRepositoryService.deleteById(history.getId());
        }

        // 팔로우 삭제
        followRepositoryService.deleteByMemberId(memberId);

        //옷 삭제
        List<Cloth> clothes = memberRepositoryService.findClothesByMemberId(memberId);

        for (Cloth cloth : clothes) {
            historyClothRepositoryService.deleteAllByClothId(cloth.getId());
            clothFolderRepositoryService.deleteAllByClothId(cloth.getId());
            clothImageRepositoryService.deleteByClothId(cloth.getId());
            clothRepositoryService.deleteById(cloth.getId());
        }

        //폴더 삭제
        List<Folder> folders = memberRepositoryService.findFoldersByMemberId(memberId);

        for(Folder folder : folders){
            folderAccessibleValidator.validateFolderAccessOfMember(folder.getId(), memberId);
            try {
                folderRepositoryService.deleteById(folder.getId());
            } catch (Exception ex) {
                throw new FolderException(ErrorStatus.FAILED_TO_DELETE_FOLDER);
            }
        }

        //댓글 삭제
        List<Comment> comments = memberRepositoryService.findCommentsByMemberId(memberId);

        for(Comment comment : comments) {
            commentRepository.deleteChildren(comment.getId());

            commentRepository.deleteById(comment.getId());
        }

        memberRepositoryService.deleteMemberById(memberId);  // 최종적으로 회원 삭제

        log.info("회원 및 관련 데이터 삭제 완료: userId={}", memberId);
    }



}

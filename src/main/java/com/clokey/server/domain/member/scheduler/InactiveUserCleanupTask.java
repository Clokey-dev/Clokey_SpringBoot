package com.clokey.server.domain.member.scheduler;

import com.clokey.server.domain.member.application.UnlinkServiceImpl;
import com.clokey.server.domain.member.application.MemberRepositoryService;
import com.clokey.server.domain.member.domain.entity.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InactiveUserCleanupTask {

    private final MemberRepositoryService memberRepositoryService;
    private final UnlinkServiceImpl logoutService;

    @Scheduled(cron = "0 0 3 * * ?")  // 매일 새벽 3시에 실행
    public void cleanupInactiveUsers() {
        log.info("비활성 회원 데이터 삭제 작업 시작");

        List<Member> inactiveUsers = memberRepositoryService.findInactiveUsersBefore(LocalDate.now().minusDays(30));
        for (Member member : inactiveUsers) {
            logoutService.deleteData(member.getId());
        }

        log.info("비활성 회원 데이터 삭제 작업 완료: {}명 삭제", inactiveUsers.size());
    }
}


package com.clokey.server.domain.history.api;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/histories")
public class HistoryRestController {

    @GetMapping("/daily/{history_id}")
    public String getHistoryById(@PathVariable("history_id") Long historyId) {
        // 서비스 호출 및 로직 처리
        return "History ID: " + historyId;
    }
}

package com.clokey.server.domain.term.dto;


import lombok.Getter;

import java.util.List;

@Getter
public class TermsRequestDTO {
    private List<TermAgreement> terms;

    @Getter
    public static class TermAgreement {
        private Long termId;   // 약관 ID
        private Boolean agreed; // 동의 여부
    }
}

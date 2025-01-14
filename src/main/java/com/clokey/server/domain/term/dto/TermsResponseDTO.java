package com.clokey.server.domain.term.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class TermsResponseDTO {
    private Long userId;
    private List<TermAgreement> terms;

    @Getter
    @Builder
    public static class TermAgreement {
        private Long termId;
        private Boolean agreed;
    }
}

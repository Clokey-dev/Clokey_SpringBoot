package com.clokey.server.domain.term.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TermResponseDTO {

    private Long userId;
    private List<TermDto> terms;

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TermDto {
        private Long termId;
        private Boolean agreed;
    }
}

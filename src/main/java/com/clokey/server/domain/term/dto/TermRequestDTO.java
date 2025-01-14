package com.clokey.server.domain.term.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

public class TermRequestDTO {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class JoinDto {

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
}

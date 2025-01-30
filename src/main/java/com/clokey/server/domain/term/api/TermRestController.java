package com.clokey.server.domain.term.api;

import com.clokey.server.domain.term.application.TermCommandService;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;
import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/terms")
public class TermRestController {

    private final TermCommandService termCommandService;

    @Operation(summary = "약관 동의 API", description = "약관동의하는 API입니다.")
    @PostMapping
    public BaseResponse<TermResponseDTO> termAgree(
            @PathVariable Long userId,
            @EssentialTermAgree @RequestBody @Valid TermRequestDTO.Join request) {

        // MemberTerm 생성
        TermResponseDTO response = termCommandService.joinTerm(userId, request);

        // 성공 응답 반환
        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_CREATED, response);
    }
}


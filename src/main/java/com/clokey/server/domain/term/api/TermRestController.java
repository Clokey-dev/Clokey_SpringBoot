package com.clokey.server.domain.term.api;

import com.clokey.server.domain.member.domain.entity.Member;
import com.clokey.server.domain.member.exception.annotation.AuthUser;
import com.clokey.server.domain.term.application.TermCommandService;
import com.clokey.server.domain.term.dto.TermRequestDTO;
import com.clokey.server.domain.term.dto.TermResponseDTO;
import com.clokey.server.domain.term.exception.annotation.EssentialTermAgree;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
public class TermRestController {

    private final TermCommandService termCommandService;

    @Operation(summary = "약관 동의 API", description = "약관동의하는 API입니다.")
    @PostMapping("/users/terms")
    public BaseResponse<TermResponseDTO> termAgree(
            @Parameter(name = "user",hidden = true) @AuthUser Member member,
            @EssentialTermAgree @RequestBody @Valid TermRequestDTO.Join request) {

        // MemberTerm 생성
        TermResponseDTO response = termCommandService.joinTerm(member.getId(), request);

        // 성공 응답 반환
        return BaseResponse.onSuccess(SuccessStatus.MEMBER_ACTION_CREATED, response);
    }

    @Operation(summary = "모든 약관 조회 API", description = "모든 약관을 조회하는 API입니다.")
    @GetMapping("/users/terms")
    public BaseResponse<List<TermResponseDTO.TermList>> getTerms() {
        // 약관 목록 조회
        List<TermResponseDTO.TermList> terms = termCommandService.getTerms();

        // 성공 응답 반환
        return BaseResponse.onSuccess(SuccessStatus.MEMBER_SUCCESS, terms);
    }
}


package com.clokey.server.domain.cloth.api;

import com.clokey.server.domain.cloth.application.ClothService;
import com.clokey.server.domain.cloth.converter.ClothConverter;
import com.clokey.server.domain.cloth.dao.ClothRepository;
import com.clokey.server.domain.cloth.dto.ClothResponseDto;
import com.clokey.server.domain.cloth.exception.annotation.ClothExist;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.model.Cloth;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import com.sun.net.httpserver.Authenticator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clothes")
@Validated
public class ClothRestController {

    private final ClothService clothService;
    private final ClothAccessibleValidator clothAccessibleValidator;
    private final ClothRepository clothRepository;

    // 옷 상세 조회 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @GetMapping("/{clothId}")
    @Operation(summary = "특정 옷을 조회할 수 있는 API", description = "path variable로 cloth_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clothId", description = "옷의 id, path variable 입니다.")
    })
    public BaseResponse<ClothResponseDto.ClothReadResult> getClothDetails(@PathVariable @Valid @ClothExist Long clothId, @RequestParam Long memberId) {

        // 멤버가 옷에 대해서 접근 권한이 있는지 확인합니다. -> 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 조회하는 현 유저를 나타냄
        clothAccessibleValidator.validateClothAccessOfMember(clothId, memberId);

        Optional<Cloth> cloth = clothService.getClothById(clothId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_SUCCESS, ClothConverter.toClothReadResult(cloth.get()));
    }
}
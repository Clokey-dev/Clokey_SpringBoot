package com.clokey.server.domain.cloth.api;

import com.clokey.server.domain.category.exception.annotation.CategoryExist;
import com.clokey.server.domain.cloth.application.ClothService;
import com.clokey.server.domain.cloth.dto.ClothRequestDTO;
import com.clokey.server.domain.cloth.dto.ClothResponseDTO;
import com.clokey.server.domain.cloth.exception.annotation.ClothCreateOrUpdateFormat;
import com.clokey.server.domain.cloth.exception.annotation.ClothExist;
import com.clokey.server.domain.cloth.exception.annotation.ClothImageFormat;
import com.clokey.server.domain.cloth.exception.annotation.ClothImagePresence;
import com.clokey.server.domain.cloth.exception.validator.ClothAccessibleValidator;
import com.clokey.server.domain.member.exception.annotation.IdValid;
import com.clokey.server.domain.member.exception.annotation.MemberExist;
import com.clokey.server.domain.model.entity.enums.ClothSort;
import com.clokey.server.domain.model.entity.enums.Season;
import com.clokey.server.global.common.response.BaseResponse;
import com.clokey.server.global.error.code.status.SuccessStatus;
import com.clokey.server.global.error.exception.annotation.CheckPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clothes")
@Validated
public class ClothRestController {

    private final ClothService clothService;
    private final ClothAccessibleValidator clothAccessibleValidator;

    // 옷장의 옷 조회 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @GetMapping("/{clokeyId}")
    @Operation(summary = "유저의 옷장을 조회하는 API", description = "path variable으로 clokeyId를 넘겨주세요" +
                                                                    "query string으로 category_id를 넘겨주세요." +
                                                                    "query string으로 season을 넘겨주세요." +
                                                                    "query string으로 sort를 넘겨주세요." +
                                                                    "query string으로 page를 넘겨주세요." +
                                                                    "query string으로 pageSize를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clokeyId", description = "클로키 id, path variable 입니다."),
            @Parameter(name = "categoryId", description = "카테고리의 id, query string 입니다."),
            @Parameter(name = "season", description = "계절(Season) ENUM 값 { SPRING, SUMMER, FALL, WINTER }, query string 입니다."),
            @Parameter(name = "sort", description = "정렬(Sort) ENUM 값 { "+
                    "//착용순 WEAR,"+
                    "//미착용순 NOT_WEAR,"+
                    "//최신등록순 LATEST,"+
                    "//오래된순 OLDEST,"+
                    "}, query string 입니다."),
            @Parameter(name = "page", description = "페이지 값, query string 입니다."),
            @Parameter(name = "size", description = "페이지에 표시할 요소 개수 값, query string 입니다.")
    })
    public BaseResponse<ClothResponseDTO.CategoryClothPreviewListResult> getClothPreviewInfoListByCategoryId(
            @PathVariable @IdValid String clokeyId,
            @RequestParam @CategoryExist Long categoryId,
            @RequestParam Season season,
            @RequestParam ClothSort sort,
            @RequestParam @CheckPage int page,
            @RequestParam int size
    ) {
        // ClothService를 통해 데이터를 가져오고, 결과 반환
        ClothResponseDTO.CategoryClothPreviewListResult result = clothService.readClothPreviewInfoListByClokeyId(clokeyId, categoryId, season, sort, page, size);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_SUCCESS, result);
    }

    // 팝업용 옷 조회 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @GetMapping("/{clothId}/popup-view")
    @Operation(summary = "특정 옷을 팝업용으로 조회하는 API", description = "path variable로 cloth_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clothId", description = "옷의 id, path variable 입니다.")
    })
    public BaseResponse<ClothResponseDTO.ClothPopupViewResult> getClothPopupInfo(
            @PathVariable @ClothExist Long clothId,
            @RequestParam @MemberExist Long memberId
    ) {
        // 멤버가 옷에 대해서 접근 권한이 있는지 확인합니다. -> 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 조회하는 현 유저를 나타냄
        clothAccessibleValidator.validateClothAccessOfMember(clothId, memberId);

        // ClothService를 통해 데이터를 가져오고, 결과 반환
        ClothResponseDTO.ClothPopupViewResult result = clothService.readClothPopupInfoById(clothId, memberId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_SUCCESS, result);
    }

    // 수정용 옷 조회 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @GetMapping("/{clothId}/edit-view")
    @Operation(summary = "특정 옷을 수정용으로 조회하는 API", description = "path variable로 cloth_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clothId", description = "옷의 id, path variable 입니다.")
    })
    public BaseResponse<ClothResponseDTO.ClothEditViewResult> getClothEditInfo(
            @PathVariable @ClothExist Long clothId,
            @RequestParam @MemberExist Long memberId
    ) {
        // 멤버가 옷에 대해서 접근 권한이 있는지 확인합니다. -> 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 조회하는 현 유저를 나타냄
        clothAccessibleValidator.validateClothAccessOfMember(clothId, memberId);

        // ClothService를 통해 데이터를 가져오고, 결과 반환
        ClothResponseDTO.ClothEditViewResult result = clothService.readClothEditInfoById(clothId, memberId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_SUCCESS, result);
    }

    // 옷 상세 조회 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @GetMapping("/{clothId}/detail-view")
    @Operation(summary = "특정 옷을 상세 조회하는 API", description = "path variable로 cloth_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_200", description = "OK, 성공적으로 조회되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clothId", description = "옷의 id, path variable 입니다.")
    })
    public BaseResponse<ClothResponseDTO.ClothDetailViewResult> getClothDetatilInfo(
            @PathVariable @ClothExist Long clothId,
            @RequestParam @MemberExist Long memberId
    ) {
        // 멤버가 옷에 대해서 접근 권한이 있는지 확인합니다. -> 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 조회하는 현 유저를 나타냄
        clothAccessibleValidator.validateClothAccessOfMember(clothId, memberId);

        // ClothService를 통해 데이터를 가져오고, 결과 반환
        ClothResponseDTO.ClothDetailViewResult result = clothService.readClothDetailInfoById(clothId, memberId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_SUCCESS, result);
    }

    // 옷 생성 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @PostMapping(value = "/", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "새로운 옷을 생성하는 API", description = "query string으로 category_id를 넘겨주세요.\nrequest body에 ClothCreateRequestDTO 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_201", description = "CREATED, 성공적으로 생성되었습니다."),
    })
    @Parameters({
            @Parameter(name = "categoryId", description = "카테고리의 id, query string 입니다.")
    })
    public BaseResponse<ClothResponseDTO.ClothCreateResult> postCloth(
            @RequestPart("clothCreateRequest") @Valid @ClothCreateOrUpdateFormat ClothRequestDTO.ClothCreateOrUpdateRequest clothCreateRequest,
            @RequestPart("imageFile") @ClothImagePresence @ClothImageFormat MultipartFile imageFile,
            @RequestParam @CategoryExist Long categoryId,
            @RequestParam @MemberExist Long memberId
    ) {
        // 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 생성하는 현 유저를 나타냄

        // ClothService를 통해 데이터를 생성하고, 결과 반환
        ClothResponseDTO.ClothCreateResult result =clothService.createCloth(categoryId, memberId, clothCreateRequest, imageFile);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_CREATED, result);
    }

    // 옷 수정 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @PatchMapping(value = "/{clothId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "특정 옷을 수정하는 API", description = "path variable로 cloth_id를 넘겨주세요.\nquery string으로 category_id를 넘겨주세요.\nrequest body에 ClothCreateOrUpdateRequestDTO 형식의 데이터를 전달해주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_204", description = "OK, 성공적으로 수정되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clothId", description = "옷의 id, path variable 입니다."),
            @Parameter(name = "categoryId", description = "카테고리의 id, query string 입니다.")
    })
    public BaseResponse<ClothResponseDTO.ClothCreateResult> patchCloth(
            @RequestPart("clothUpdateRequest") @Valid @ClothCreateOrUpdateFormat ClothRequestDTO.ClothCreateOrUpdateRequest clothUpdateRequest,
            @RequestPart(value = "imageFile", required = false) @ClothImageFormat MultipartFile imageFile,
            @PathVariable @ClothExist Long clothId,
            @RequestParam @CategoryExist Long categoryId,
            @RequestParam @MemberExist Long memberId
    ) {
        // 멤버가 옷에 대해서 수정 권한이 있는지 확인합니다. -> 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 삭제하는 현 유저를 나타냄
        clothAccessibleValidator.validateClothOfMember(clothId, memberId);

        // ClothService를 통해 데이터를 수정
        clothService.updateClothById(clothId, categoryId, memberId, clothUpdateRequest, imageFile);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_EDITED, null);
    }

    // 옷 삭제 API, 사용자 토큰 받는 부분 추가 및 변경해야함
    @DeleteMapping("/{clothId}")
    @Operation(summary = "특정 옷을 삭제하는 API", description = "path variable로 cloth_id를 넘겨주세요.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "CLOTH_204", description = "OK, 성공적으로 삭제되었습니다."),
    })
    @Parameters({
            @Parameter(name = "clothId", description = "옷의 id, path variable 입니다.")
    })
    public BaseResponse<Void> deleteCloth(
            @PathVariable @ClothExist Long clothId,
            @RequestParam @MemberExist Long memberId
            ) {
        // 멤버가 옷에 대해서 수정 권한이 있는지 확인합니다. -> 토큰을 이용해서 현재 로그인 중인 memberId 뽑아와서 넣어줄 것. 삭제하는 현 유저를 나타냄
        clothAccessibleValidator.validateClothOfMember(clothId, memberId);

        // ClothService를 통해 데이터를 삭제
        clothService.deleteClothById(clothId);

        return BaseResponse.onSuccess(SuccessStatus.CLOTH_DELETED, null);
    }
}

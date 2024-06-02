package com.example.coffies_vol_02.place.controller.api;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import javax.validation.Valid;

@Log4j2
@Api(tags = "Place Api Controller", value = "가게 관련 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/place")
public class PlaceApiController {

    private final PlaceService placeService;

    @Operation(summary = "가게 목록 조회", description = "가게 목록을 조회한다",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = PlaceResponseDto.class)))
    })
    @GetMapping(path = "/")
    public CommonResponse<Slice<PlaceResponseDto>>listCafePLace(@ApiIgnore @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                       @Parameter(name = "keyword",description = "가게 검색어 저장", in = ParameterIn.QUERY)
                                       @RequestParam(value = "keyword", required = false) String keyword,
                                       @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        try{
            Slice<PlaceResponseDto> list = placeService.listCafePlace(pageable,keyword,customUserDetails.getMember());

            return new CommonResponse<>(HttpStatus.OK.value(), list);
        }catch (Exception e){
            log.info(e.getMessage());
        }
        return new CommonResponse<>(HttpStatus.OK.value(),placeService.listCafePlace(pageable,keyword,null));
    }

    @Operation(summary = "가게 목록 검색", description = "가게 목록페이지에서 가게를 검색을 한다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = PlaceResponseDto.class)))
    })
    @GetMapping(path = "/search")
    public CommonResponse<?> searchCafePLace(@Parameter(name = "searchType",description = "가게 검색타입",required = true)
                                             @RequestParam(value = "searchType",required = false) String searchType,
                                             @Parameter(name = "placeKeyword",description = "redis에 저장된 검색어",in = ParameterIn.QUERY)
                                             @RequestParam(value = "placeKeyword",required = false) String keyword,
                                             @ApiIgnore @PageableDefault(sort = "id", size=5 ,direction = Sort.Direction.DESC) Pageable pageable,
                                             @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        Slice<PlaceResponseDto> list = placeService.searchCafePlace(SearchType.toType(searchType),keyword, pageable, customUserDetails.getMember());

        //검색어가 없는 경우
        if(StringUtils.isBlank(keyword)){
            return new CommonResponse<>(HttpStatus.OK.value(),ERRORCODE.NOT_SEARCH_VALUE.getMessage());
        }

        return new CommonResponse<>(HttpStatus.OK.value(), list);
    }

    @Operation(summary = "가게 단일 조회", description = "가게 목록에서 가게를 조회를 한다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = PlaceResponseDto.class)))
    })
    @GetMapping(path = "/{place-id}")
    public CommonResponse<PlaceResponseDto> findCafePlaceById(@Parameter(name = "place-id",description = "가게 생성번호",required = true,in = ParameterIn.PATH)
                                                        @PathVariable("place-id") Integer placeId) {

        PlaceResponseDto placeDetail = placeService.findCafePlaceById(placeId);

        return new CommonResponse<>(HttpStatus.OK.value(), placeDetail);
    }

    @Operation(summary = "가게 등록", description = "어드민 페이지에서 가게를 등록을 한다.")
    @PostMapping(path = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer> placeRegister(@RequestBody(description = "가게 요청 dto",required = true)
                                                 @Valid @ModelAttribute PlaceRequestDto dto,
                                                 @ModelAttribute PlaceImageRequestDto imageRequestDto,
                                                 BindingResult bindingResult)throws Exception {

        Integer registerResult = placeService.createCafePlace(dto, imageRequestDto);

        if(registerResult>0){
            return new CommonResponse<>(HttpStatus.OK.value(), registerResult);
        }else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "가게 등록에 실패했습니다.");
        }
    }

    @Operation(summary = "가게 수정", description = "등록된 가게를 수정을 한다.")
    @PutMapping(path = "/{place-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonResponse<Integer> placeUpdate(@Parameter(name = "place-id",description = "가게 생성번호",required = true,in = ParameterIn.PATH)
                                               @PathVariable("place-id") Integer placeId,
                                               @ModelAttribute PlaceRequestDto dto,
                                               @ModelAttribute PlaceImageRequestDto imageRequestDto)throws Exception {

        Integer placeUpdateResult = placeService.updateCafePlace(placeId, dto, imageRequestDto);

        return new CommonResponse<>(HttpStatus.OK.value(), placeUpdateResult);
    }

    @Operation(summary = "가게 삭제", description = "등록된 가게를 삭제한다.")
    @DeleteMapping(path = "/{place-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?> placeDelete(@Parameter(name = "place-id",description = "가게 생성번호",required = true)
                                         @PathVariable("place-id") Integer placeId)throws Exception {

        placeService.deleteCafePlace(placeId);

        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }

    @Operation(summary = "가게 목록 top5 조회", description = "가게 목록을 조회한다",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = PlaceResponseDto.class)))
    })
    @GetMapping(path = "/top5list")
    public CommonResponse<Page<PlaceResponseDto>> placeTop5List(@ApiIgnore @PageableDefault Pageable pageable) {

        Page<PlaceResponseDto> top5list = placeService.cafePlaceByReviewRateTop5(pageable);

        return new CommonResponse<>(HttpStatus.OK.value(), top5list);
    }

}

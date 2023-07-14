package com.example.coffies_vol_02.place.controller.api;

import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceImageRequestDto;
import com.example.coffies_vol_02.place.domain.dto.request.PlaceRequestDto;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    @ApiOperation(value = "가게 목록 조회", notes = "가게 목록을 조회한다")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "keyword",dataType = "String",required = false,value = "가게 검색어")
    })
    @GetMapping(path = "/list")
    public CommonResponse<Slice<PlaceResponseDto>> placeList(@ApiIgnore @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                             @RequestParam(value = "keyword", required = false) String keyword,
                                                             @RequestParam(value = "searchType",required = false)String searchType,
                                                             @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Slice<PlaceResponseDto> list = null;

        try {
            list = placeService.placeSlideList(pageable,keyword,searchType,customUserDetails.getMember());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), list);
    }

    @ApiOperation(value = "가게 목록 검색", notes = "가게 목록페이지에서 가게를 검색을 한다.")
    @ApiImplicitParam(value = "keyword",dataType = "String",required = false,name = "가게 검색어")
    @GetMapping(path = "/search")
    public CommonResponse<?> placeListSearch(@RequestParam(value = "placeKeyword",required = false) String keyword,
                                                                  @ApiIgnore @PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                                  @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        Page<PlaceResponseDto> list = null;

        try {
            list = placeService.placeListAll(keyword, pageable, customUserDetails.getMember());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //검색어가 없는 경우
        if(keyword==null||keyword==""){
            return new CommonResponse<>(HttpStatus.OK.value(),ERRORCODE.NOT_SEARCH_VALUE.getMessage());
        }

        return new CommonResponse<>(HttpStatus.OK.value(), list);
    }

    @ApiOperation(value = "가게 단일 조회", notes = "가게 목록에서 가게를 조회를 한다.")
    @ApiImplicitParam(value = "place_id",dataType = "Integer",required = true,name = "가게 번호")
    @GetMapping(path = "/detail/{place_id}")
    public CommonResponse<PlaceResponseDto> placeDetail(@PathVariable("place_id") Integer placeId) {
        PlaceResponseDto placeDetail = new PlaceResponseDto();

        try {
            placeDetail = placeService.placeDetail(placeId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(), placeDetail);
    }

    @ApiOperation(value = "가게 등록", notes = "어드민 페이지에서 가게를 등록을 한다.")
    @PostMapping(path = "/register", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer> placeRegister(@Valid @ModelAttribute PlaceRequestDto dto,
                                                 @ModelAttribute PlaceImageRequestDto imageRequestDto, BindingResult bindingResult) {
        Integer registerResult = 0;

        try {
            registerResult = placeService.placeRegister(dto, imageRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(), registerResult);
    }

    @ApiOperation(value = "가게 수정", notes = "등록된 가게를 수정을 한다.")
    @ApiImplicitParam(name = "place_id",value = "place_id",dataType = "Integer",required = true)
    @PutMapping(path = "/update/{place_id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonResponse<Integer> placeUpdate(@PathVariable("place_id") Integer placeId,
                                               @ModelAttribute PlaceRequestDto dto,
                                               @ModelAttribute PlaceImageRequestDto imageRequestDto) {
        Integer placeUpdateResult = 0;

        try {
            placeUpdateResult = placeService.placeModify(placeId, dto, imageRequestDto);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), placeUpdateResult);
    }

    @ApiOperation(value = "가게 삭제", notes = "등록된 가게를 삭제한다.")
    @ApiImplicitParam(name = "place_id",value = "place_id",dataType = "Integer",required = true)
    @DeleteMapping(path = "/delete/{place_id}")
    public CommonResponse<?> placeDelete(@PathVariable("place_id") Integer placeId) {

        try {
            placeService.placeDelete(placeId);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }

    @ApiOperation(value = "가게 목록 top5 조회", notes = "가게 목록을 조회한다")
    @GetMapping(path = "/top5list")
    public CommonResponse<Page<PlaceResponseDto>> placeTop5List(@ApiIgnore @PageableDefault Pageable pageable) {
        Page<PlaceResponseDto> top5list = null;

        try {
            top5list = placeService.placeTop5(pageable);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), top5list);
    }
}

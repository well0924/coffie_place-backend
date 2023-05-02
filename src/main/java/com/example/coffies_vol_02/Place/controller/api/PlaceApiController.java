package com.example.coffies_vol_02.Place.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.Place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Log4j2
@Api(tags = "Place Api Controller",value = "가게 관련 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/place")
public class PlaceApiController {
    private final PlaceService placeService;
    
    @ApiOperation(value = "가게 목록 조회")
    @GetMapping(path = "/list")
    public CommonResponse<Page<PlaceDto.PlaceResponseDto>>placeList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<PlaceDto.PlaceResponseDto> list = placeService.placeList(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "가게 목록 검색")
    @GetMapping(path = "/search")
    public CommonResponse<Page<PlaceDto.PlaceResponseDto>>placeListSearch(@RequestParam String keyword,@PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<PlaceDto.PlaceResponseDto> list = placeService.placeListAll(keyword,pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "가게 단일 조회")
    @GetMapping(path = "/detail/{place_id}")
    public CommonResponse<PlaceDto.PlaceResponseDto>placeDetail(@PathVariable("place_id")Integer placeId){
        PlaceDto.PlaceResponseDto placeDetail = placeService.placeDetail(placeId);
        return new CommonResponse<>(HttpStatus.OK.value(),placeDetail);
    }
    
    @ApiOperation(value = "가게 등록")
    @PostMapping(path="/register",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>placeRegister(@Valid @ModelAttribute PlaceDto.PlaceRequestDto dto, @ModelAttribute PlaceImageDto.PlaceImageRequestDto imageRequestDto, BindingResult bindingResult) throws Exception {
        int registerResult = 0;
        registerResult = placeService.placeRegister(dto,imageRequestDto);
        return new CommonResponse<>(HttpStatus.OK.value(),registerResult);
    }
    
    @ApiOperation(value = "가게 수정")
    @PutMapping(path = "/update/{place_id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public CommonResponse<Integer>placeUpdate(@PathVariable("place_id") Integer placeId, @ModelAttribute PlaceDto.PlaceRequestDto dto, @ModelAttribute PlaceImageDto.PlaceImageRequestDto imageRequestDto) throws Exception {
        int placeUpdateResult = 0;
        placeUpdateResult = placeService.placeModify(placeId,dto,imageRequestDto);
        return new CommonResponse<>(HttpStatus.OK.value(),placeUpdateResult);
    }

    @DeleteMapping(path = "/delete/{place_id}")
    public CommonResponse<?>placeDelete(@PathVariable("place_id")Integer placeId) throws Exception {
        placeService.placeDelete(placeId);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}

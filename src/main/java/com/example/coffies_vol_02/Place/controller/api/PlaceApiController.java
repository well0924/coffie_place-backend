package com.example.coffies_vol_02.Place.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.Place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Place Api Controller",value = "가게 관련 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/place")
public class PlaceApiController {
    private final PlaceService placeService;
    
    @ApiOperation(value = "가게 목록 조회")
    @GetMapping("/list")
    public CommonResponse<Page<PlaceDto.PlaceResponseDto>>placeList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<PlaceDto.PlaceResponseDto> list = null;

        try {
            list = placeService.placeList(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "가게 단일 조회")
    @GetMapping("/detail/{place_id}")
    public CommonResponse<PlaceDto.PlaceResponseDto>placeDetail(@PathVariable("place_id")Integer placeId){
        PlaceDto.PlaceResponseDto placeDetail = new PlaceDto.PlaceResponseDto();

        try {
            placeDetail = placeService.placeDetail(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),placeDetail);
    }
    
    @ApiOperation(value = "가게 등록")
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>placeRegister(@Valid @ModelAttribute PlaceDto.PlaceRequestDto dto, @ModelAttribute PlaceImageDto.PlaceImageRequestDto image, BindingResult bindingResult){
        int registerResult = 0;

        try {
            registerResult = placeService.placeRegister(dto,image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new CommonResponse<>(HttpStatus.OK.value(),registerResult);
    }
    
    @ApiOperation(value = "가게 수정")
    @PutMapping("/updateplace/{place_id}")
    public CommonResponse<Integer>placeUpdate(@PathVariable("place_id") Integer placeId, @ModelAttribute PlaceDto.PlaceRequestDto dto, @ModelAttribute PlaceImageDto.PlaceImageRequestDto imageRequestDto){
        int placeUpdateResult = 0;

        try {
            placeUpdateResult = placeService.placeModify(placeId,dto,imageRequestDto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),placeUpdateResult);
    }

    @DeleteMapping("/placeDelete/{place_id}")
    public CommonResponse<?>placeDelete(@PathVariable("place_id")Integer placeId){

        try {
            placeService.placeDelete(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}

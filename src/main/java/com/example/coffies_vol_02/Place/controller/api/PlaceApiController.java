package com.example.coffies_vol_02.Place.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.Place.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@AllArgsConstructor
@RequestMapping("/api/place")
public class PlaceApiController {
    private final PlaceService placeService;

    @GetMapping("/list")
    public CommonResponse<?>placeList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<PlaceDto.PlaceResponseDto> list = placeService.placeList(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    @GetMapping("/detail/{place_id}")
    public CommonResponse<?>placeDetail(@PathVariable("place_id")Integer placeId){
        PlaceDto.PlaceResponseDto placeDetail = placeService.placeDetail(placeId);
        return new CommonResponse<>(HttpStatus.OK.value(),placeDetail);
    }
    @PostMapping("/register")
    public CommonResponse<?>placeRegister(@Valid @ModelAttribute PlaceDto.PlaceRequestDto dto, @ModelAttribute PlaceImageDto.PlaceImageRequestDto image, BindingResult bindingResult) throws Exception {

        int registerResult = 0;

        try {
            registerResult = placeService.placeRegister(dto,image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return new CommonResponse<>(HttpStatus.OK.value(),registerResult);
    }

    @PutMapping("/updateplace/{place_id}")
    public CommonResponse<?>placeUpdate(@PathVariable("place_id") Integer placeId, @ModelAttribute PlaceDto.PlaceRequestDto dto, @ModelAttribute PlaceImageDto.PlaceImageRequestDto imageRequestDto) throws Exception {
        int placeUpdateResult = 0;

        try {
            placeUpdateResult = placeService.placeModify(placeId,dto,imageRequestDto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),placeUpdateResult);
    }

    @DeleteMapping("/placeDelete/{place_id}")
    public CommonResponse<?>placeDelete(@PathVariable("place_id")Integer placeId) throws Exception {
        placeService.placeDelete(placeId);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}

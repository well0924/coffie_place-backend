package com.example.coffies_vol_02.Place.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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
    public CommonResponse<?>placeRegister(@RequestBody PlaceDto.PlaceRequestDto dto){
        return new CommonResponse<>();
    }

    @PutMapping("/updateplace/{place_id}")
    public CommonResponse<?>placeUpdate(@PathVariable("place_id") Integer placeId,@RequestBody PlaceDto.PlaceRequestDto dto){
        return new CommonResponse<>();
    }

    @DeleteMapping("/placeDelete/{place_id}")
    public CommonResponse<?>placeDelete(@PathVariable("place_id")Integer placeId){
        return new CommonResponse<>();
    }
}

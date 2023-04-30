package com.example.coffies_vol_02.Place.controller.view;

import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.Place.service.PlaceImageService;
import com.example.coffies_vol_02.Place.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@AllArgsConstructor
@RequestMapping("/page/place")
public class PlaceViewController {
    private final PlaceService placeService;
    private final PlaceImageService placeImageService;

    @GetMapping("/list")
    public ModelAndView placeList(Pageable pageable){
        ModelAndView mv = new ModelAndView();

        Page<PlaceDto.PlaceResponseDto>placeList = placeService.placeList(pageable);

        mv.addObject("placelist",placeList);
        mv.setViewName("/place/placelist");

        return mv;
    }

    @GetMapping("/detail/{place_id}")
    public ModelAndView placeDetail(@PathVariable("place_id") Integer placeId) throws Exception {
        ModelAndView mv = new ModelAndView();

        PlaceDto.PlaceResponseDto detail  = placeService.placeDetail(placeId);
        List<PlaceImageDto.PlaceImageResponseDto> imageResponseDtoList = placeImageService.placeImageResponseDtoList(placeId);

        mv.addObject("detail",detail);
        mv.addObject("imagelist",imageResponseDtoList);

        mv.setViewName("/place/placedetail");

        return mv;
    }

    @GetMapping("/placeregister")
    public ModelAndView placeRegister(){
        ModelAndView mv = new ModelAndView();

        String uuid = UUID.randomUUID().toString();
        String key = "place_"+uuid.substring(0,uuid.indexOf("-"));

        mv.addObject("fileGroupId",key);
        mv.setViewName("/place/placeregister");

        return mv;
    }

    @GetMapping("/placemodify/{place_id}")
    public ModelAndView placeModify(@PathVariable("place_id")Integer placeId){
        ModelAndView mv = new ModelAndView();

        PlaceDto.PlaceResponseDto detail = placeService.placeDetail(placeId);

        mv.addObject("detail",detail);
        mv.setViewName("/place/placemodify");

        return mv;
    }
}

package com.example.coffies_vol_02.place.controller.view;

import com.example.coffies_vol_02.place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.place.domain.dto.PlaceImageDto;
import com.example.coffies_vol_02.place.service.PlaceImageService;
import com.example.coffies_vol_02.place.service.PlaceService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/page/place")
public class PlaceViewController {
    private final PlaceService placeService;
    private final PlaceImageService placeImageService;

    @GetMapping("/list")
    public ModelAndView placeList(Pageable pageable,String keyword){

        ModelAndView mv = new ModelAndView();

        Slice<PlaceDto.PlaceResponseDto>list = null;

        try{
            //placeList = placeService.placeList(pageable);
            list = placeService.placeSlideList(pageable,keyword);
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("placelist",list);
        mv.addObject("keyword",keyword);
        mv.setViewName("/place/placelist");

        return mv;
    }

    @GetMapping("/detail/{place_id}")
    public ModelAndView placeDetail(@PathVariable("place_id") Integer placeId){
        ModelAndView mv = new ModelAndView();

        PlaceDto.PlaceResponseDto detail  = new PlaceDto.PlaceResponseDto();
        List<PlaceImageDto.PlaceImageResponseDto> imageResponseDtoList = new ArrayList<>();

        try{
            detail  = placeService.placeDetail(placeId);
            imageResponseDtoList = placeImageService.placeImageResponseDtoList(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }

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

        PlaceDto.PlaceResponseDto detail = new PlaceDto.PlaceResponseDto();

        try{
            detail = placeService.placeDetail(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("detail",detail);
        mv.setViewName("/place/placemodify");

        return mv;
    }
}

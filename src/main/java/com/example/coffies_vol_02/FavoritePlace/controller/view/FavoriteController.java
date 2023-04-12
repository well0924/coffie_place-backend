package com.example.coffies_vol_02.FavoritePlace.controller.view;

import com.example.coffies_vol_02.FavoritePlace.service.FavoritePlaceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/page/mypage")
public class FavoriteController {
    private final FavoritePlaceService favoritePlaceService;

    @GetMapping
    public ModelAndView myContents(){
        ModelAndView mv = new ModelAndView();
        return mv;
    }
}

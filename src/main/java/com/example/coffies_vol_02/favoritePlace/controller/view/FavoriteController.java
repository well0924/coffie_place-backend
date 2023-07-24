package com.example.coffies_vol_02.favoritePlace.controller.view;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.service.PlaceService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/page/mypage")
public class FavoriteController {
    private final FavoritePlaceService favoritePlaceService;

    private final PlaceService placeService;

    @GetMapping("/contents/{id}")
    public ModelAndView myContents(@PathVariable("id")String userId, @PageableDefault(direction = Sort.Direction.DESC,size = 5,sort = "id") Pageable pageable){
        ModelAndView mv = new ModelAndView();
        Page<BoardResponse> list = null;

        try {
            list = favoritePlaceService.getMyPageBoardList(pageable, userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("mylist",list);
        mv.setViewName("/mypage/myarticle");
        return mv;
    }

    @GetMapping("/comment/{id}")
    public ModelAndView myComment(@PathVariable("id")String userId, @PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        ModelAndView mv = new ModelAndView();
        List<placeCommentResponseDto> list = new ArrayList<>();

        try {
            list = favoritePlaceService.getMyPageCommnetList(userId,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("mylist",list);
        mv.setViewName("/mypage/mycomment");
        return mv;
    }

    @GetMapping("/page/{user_id}")
    public ModelAndView myWishList(@PageableDefault Pageable pageable,@PathVariable("user_id")String userId){
        ModelAndView mv = new ModelAndView();

        Page<FavoritePlaceResponseDto>list = null;

        try{
            list = favoritePlaceService.MyWishList(pageable,userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("wishlist",list);
        mv.setViewName("/mypage/wishlist");

        return mv;
    }

    @GetMapping("/nearplace")
    public ModelAndView nearPlaceList(@AuthenticationPrincipal CustomUserDetails customUserDetails){
        ModelAndView mv = new ModelAndView();
        List<PlaceResponseDto> near5 = null;
        //근처 가게 top5
        if (customUserDetails != null) {
            near5  = placeService.placeNear(customUserDetails.getMember().getMemberLat(), customUserDetails.getMember().getMemberLng());
        }

        mv.addObject("near5",near5);
        mv.setViewName("/mypage/nearPlaceList");

        return mv;
    }
}

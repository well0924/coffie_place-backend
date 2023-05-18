package com.example.coffies_vol_02.FavoritePlace.controller.view;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.FavoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.FavoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.Place.service.PlaceImageService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
    private final PlaceImageService placeImageService;

    @GetMapping("/contents/{id}")
    public ModelAndView myContents(@PathVariable("id")String userId, @PageableDefault(direction = Sort.Direction.DESC,size = 5,sort = "id") Pageable pageable){
        ModelAndView mv = new ModelAndView();
        Page<BoardDto.BoardResponseDto> list = null;

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
        List<CommentDto.CommentResponseDto> list = new ArrayList<>();

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

        Page<FavoritePlaceDto.FavoriteResponseDto>list = favoritePlaceService.MyWishList(pageable,userId);

        mv.addObject("wishlist",list);
        mv.setViewName("/mypage/wishlist");

        return mv;
    }
}

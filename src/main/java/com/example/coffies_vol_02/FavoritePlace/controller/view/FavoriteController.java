package com.example.coffies_vol_02.FavoritePlace.controller.view;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.FavoritePlace.service.FavoritePlaceService;
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
    private final CommentRepository commentRepository;


    @GetMapping("/contents/{id}")
    public ModelAndView myContents(@PathVariable("id")String userId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @PageableDefault(direction = Sort.Direction.DESC,size = 5,sort = "id") Pageable pageable){
        ModelAndView mv = new ModelAndView();
        Page<BoardDto.BoardResponseDto> list = null;
        try {
            list = favoritePlaceService.getMyPageBoardList(pageable,customUserDetails.getMember(),userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("mylist",list);
        mv.setViewName("/mypage/myarticle");
        return mv;
    }

    @GetMapping("/comment/{id}")
    public ModelAndView myComment(@PathVariable("id")String userId,@AuthenticationPrincipal CustomUserDetails customUserDetails,@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        ModelAndView mv = new ModelAndView();
        List<CommentDto.CommentResponseDto> list = new ArrayList<>();

        try {
            list = favoritePlaceService.getMyPageCommnetList(userId,pageable,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("mylist",list);
        mv.setViewName("/mypage/mycomment");
        return mv;
    }


}

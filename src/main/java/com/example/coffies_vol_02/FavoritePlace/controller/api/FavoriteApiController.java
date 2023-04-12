package com.example.coffies_vol_02.FavoritePlace.controller.api;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.FavoritePlace.service.FavoritePlaceService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class FavoriteApiController {
    private FavoritePlaceService favoritePlaceService;

    @ApiOperation(value = "로그인한 회원이 작성한 글")
    @GetMapping("/contents")
    public CommonResponse<Page<BoardDto.BoardResponseDto>>MyArticle(@AuthenticationPrincipal CustomUserDetails customUserDetails,@PageableDefault Pageable pageable){
        Page<BoardDto.BoardResponseDto>list = null;

        try {
            list = favoritePlaceService.getMyPageBoardList(pageable,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "로그인한 회원이 작성한 댓글")
    @GetMapping("/comment")
    public CommonResponse<List<CommentDto.CommentResponseDto>>MyComment(@AuthenticationPrincipal CustomUserDetails customUserDetails,Pageable pageable){
        List<CommentDto.CommentResponseDto>list = new ArrayList<>();

        try {
            list = favoritePlaceService.getMyPageCommnetList(pageable,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}

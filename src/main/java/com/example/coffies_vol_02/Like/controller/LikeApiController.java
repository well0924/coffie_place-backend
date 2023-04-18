package com.example.coffies_vol_02.Like.controller;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Like.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "Like Api Controller",value = "좋아요 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/like")
public class LikeApiController {
    private final LikeService likeService;

    @ApiOperation("게시글 좋아요 +1")
    @PostMapping("/plus/{board_id}")
    public CommonResponse<?>createLike(@PathVariable("board_id")Integer boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result ="";

        if(customUserDetails.getMember()!=null){
            result = likeService.createBoardLike(boardId,customUserDetails.getMember());
        }

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @ApiOperation("게시글 좋아요 취소")
    @DeleteMapping("/minus/{board_id}")
    public CommonResponse<String>boardLikeCancel(@PathVariable("board_id")Integer boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = "";

        if(customUserDetails.getMember()!=null){
            result=likeService.cancelLike(boardId,customUserDetails.getMember());
        }

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @ApiOperation("게시글 좋아요 카운트")
    @GetMapping("/{board_id}")
    public CommonResponse<List<String>>boardLikeCount(@PathVariable("board_id")Integer boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails){

        List<String>resultDate = likeService.likeCount(boardId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK.value(),resultDate);
    }

    @ApiOperation("가게 댓글 좋아요 카운트")
    public CommonResponse<?>placeLikeCount(){
        return new CommonResponse<>();
    }
}

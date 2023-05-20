package com.example.coffies_vol_02.like.controller;

import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.like.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Api(tags = "Like Api Controller",value = "좋아요 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/like")
public class LikeApiController {
    private final LikeService likeService;
    private final RedisService redisService;

    @Operation(summary = "게시글 좋아요 +1",description = "게시글 조회 페이지에서 좋아요기능을 추가합니다.")
    @PostMapping(path = "/plus/{board_id}")
    public CommonResponse<?>createLike(@PathVariable("board_id")Integer boardId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if(customUserDetails.getMember()!=null){
            String result = likeService.createBoardLike(boardId, customUserDetails.getMember());
            return new CommonResponse<>(HttpStatus.OK.value(),result);
        }else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "Fail!");
        }
    }

    @Operation(summary = "게시글 좋아요 -1",description = "게시글 조회 페이지에서 좋아요기능을 취소합니다.")
    @DeleteMapping(path = "/minus/{board_id}")
    public CommonResponse<String>boardLikeCancel(@PathVariable("board_id")Integer boardId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        if(customUserDetails.getMember()!=null){
            String result =likeService.cancelLike(boardId,customUserDetails.getMember());
            return new CommonResponse<>(HttpStatus.OK.value(),result);
        }else{
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(),"Fail");
        }
    }

    @Operation(summary = "게시글 좋아요 카운트",description = "게시글 조회화면에서 좋아요를 추가한 수를 보여준다.")
    @GetMapping(path = "/{board_id}")
    public CommonResponse<List<String>>boardLikeCount(@PathVariable("board_id")Integer boardId,@ApiIgnore CustomUserDetails customUserDetails){

        List<String>resultDate = likeService.likeCount(boardId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK.value(),resultDate);
    }

    @Operation(summary = "댓글 좋아요 +1",description = "가게 댓글에서 좋아요를 하면 좋아요를 추가한다.")
    @PostMapping(path = "/plus/{place_id}/{reply_id}")
    public CommonResponse<?>commentLikePlus(@PathVariable("place_id")Integer placeId,@PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = likeService.commentLikePlus(placeId,replyId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @Operation(summary = "댓글 좋아요 -1",description = "좋아요를 누르면 좋아요를 줄인다.")
    @DeleteMapping(path = "/minus/{place_id}/{reply_id}")
    public CommonResponse<?>commentLikeMinus(@PathVariable("place_id")Integer placeId,@PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = likeService.commentLikeMinus(placeId,replyId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @Operation(summary = "댓글 좋아요 카운트",description = "")
    @GetMapping(path = "/comment/{reply_id}")
    public CommonResponse<List<String>>commentLikeCount(@PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<String>resultDate = likeService.likeCommentCount(replyId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),resultDate);
    }
}

package com.example.coffies_vol_02.like.controller;

import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.like.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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

    @Operation(summary = "게시글 좋아요 +1",description = "게시글 조회 페이지에서 좋아요기능을 추가합니다.")
    @PostMapping(path = "/plus/{board_id}")
    public CommonResponse<String>createLike(@Parameter(name = "board_id",description = "게시글의 번호",required = true)
                                            @PathVariable("board_id")Integer boardId,
                                            @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if(customUserDetails.getMember()!=null){
            String result = likeService.createBoardLike(boardId, customUserDetails.getMember());
            return new CommonResponse<>(HttpStatus.OK.value(),result);
        }else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "Fail!");
        }
    }

    @Operation(summary = "게시글 좋아요 -1", description = "게시글 조회 페이지에서 좋아요기능을 취소합니다.",responses = {
            @ApiResponse(responseCode = "204")
    })
    @DeleteMapping(path = "/minus/{board_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<String>boardLikeCancel(@Parameter(name = "board_id",description = "게시글의 번호",required = true)
                                                 @PathVariable("board_id")Integer boardId,
                                                 @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        if(customUserDetails.getMember()!=null){
            String result =likeService.cancelLike(boardId,customUserDetails.getMember());
            return new CommonResponse<>(HttpStatus.OK.value(),result);
        }else{
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(),"Fail");
        }
    }

    @Operation(summary = "게시글 좋아요 카운트",description = "게시글 조회화면에서 좋아요를 추가한 수를 보여준다.")
    @GetMapping(path = "/board/{board_id}")
    public CommonResponse<List<String>>boardLikeCount(@Parameter(name = "board-id",description = "게시글의 번호",required = true)
                                                      @PathVariable("board_id")Integer boardId,
                                                      @ApiIgnore CustomUserDetails customUserDetails){
        List<String>resultDate = likeService.likeCount(boardId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),resultDate);
    }

    @Operation(summary = "댓글 좋아요 +1",description = "가게 댓글에 좋아요를 하면 좋아요를 추가한다.")
    @PostMapping(path = "/comment/plus/{reply_id}")
    public CommonResponse<String>commentLikePlus(@Parameter(name = "reply_id",description = "댓글의 번호",required = true)
                                                 @PathVariable("reply_id")Integer replyId,
                                                 @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        if(customUserDetails.getMember()!=null){
            String result = likeService.commentLikePlus(replyId,customUserDetails.getMember());
            return new CommonResponse<>(HttpStatus.OK.value(),result);
        }else{
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "Fail");
        }
    }

    @Operation(summary = "댓글 좋아요 -1", description = "가게 댓글에 좋아요를 누르면 좋아요를 줄인다.")
    @DeleteMapping(path = "/comment/minus/{reply_id}")
    public CommonResponse<?>commentLikeMinus(@Parameter(name = "reply_id",description = "댓글의 번호",required = true)
                                             @PathVariable("reply_id")Integer replyId,
                                             @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        if(customUserDetails.getMember()!=null){
            String result = likeService.commentLikeMinus(replyId,customUserDetails.getMember());
            return new CommonResponse<>(HttpStatus.OK.value(),result);
        }else{
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(), "Fail");
        }
    }

    @Operation(summary = "댓글 좋아요 카운트", description = "로그인을 한 회원이 가게 댓글에 좋아요를 체크하는 기능")
    @GetMapping(path = "/comment/{reply_id}")
    public CommonResponse<List<String>>commentLikeCount(@Parameter(name = "reply_id",description = "댓글의 번호",required = true)
                                                        @PathVariable("reply_id")Integer replyId,
                                                        @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<String>resultDate = likeService.likeCommentCount(replyId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),resultDate);
    }
}

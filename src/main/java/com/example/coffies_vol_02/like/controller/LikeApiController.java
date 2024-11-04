package com.example.coffies_vol_02.like.controller;

import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.like.service.LikeService;
import com.example.coffies_vol_02.member.domain.Member;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpSession;
import java.util.List;

@Api(tags = "Like Api Controller",value = "좋아요 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/like")
public class LikeApiController {

    private final LikeService likeService;

    @Operation(summary = "게시글 좋아요 +1",description = "게시글 조회 페이지에서 좋아요기능을 추가합니다.")
    @PostMapping(path = "/plus/{board-id}")
    public CommonResponse<String>plusBoardLike(@Parameter(name = "board-id",description = "게시글의 번호",required = true)
                                            @PathVariable("board-id")Integer boardId,
                                            @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        if(customUserDetails.getMember()!=null) {
            likeService.boardLikePlus(boardId, customUserDetails.getMember().getId());
            return new CommonResponse<>(HttpStatus.OK,"게시글에 좋아요가 추가되었습니다.");
        } else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.LIKE_FAIL);
        }
    }

    @Operation(summary = "게시글 좋아요 -1", description = "게시글 조회 페이지에서 좋아요기능을 취소합니다.",responses = {
            @ApiResponse(responseCode = "204")
    })
    @DeleteMapping(path = "/minus/{board-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<String>minusBoardLike(@Parameter(name = "board-id",description = "게시글의 번호",required = true)
                                                 @PathVariable("board-id")Integer boardId,
                                                 @ApiIgnore HttpSession session){
        Member member = (Member)session.getAttribute("member");

        if(member != null) {
            likeService.boardLikeMinus(boardId,member.getId());
            return new CommonResponse<>(HttpStatus.OK,"좋아요가 취소되었습니다.");
        } else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.LIKE_FAIL);
        }
    }

    @Operation(summary = "게시글 좋아요 카운트",description = "게시글 조회화면에서 좋아요를 추가한 수를 보여준다.")
    @GetMapping(path = "/board/{board-id}")
    public CommonResponse<List<String>>countBoardLike(@Parameter(name ="board-id",description = "게시글의 번호",required = true)
                                                      @PathVariable("board-id")Integer boardId,
                                                      @ApiIgnore HttpSession session){

        Member member = (Member)session.getAttribute("member");

        if(member != null) {
            List<String>resultDate = likeService.likeCount(boardId,member);
            return new CommonResponse<>(HttpStatus.OK,resultDate);
        }

        return new CommonResponse<>(HttpStatus.UNAUTHORIZED,ERRORCODE.NOT_AUTH);
    }

    @Operation(summary = "댓글 좋아요 +1",description = "가게 댓글에 좋아요를 하면 좋아요를 추가한다.")
    @PostMapping(path = "/comment/plus/{reply-id}")
    public CommonResponse<String>plusCommentLike(@Parameter(name ="reply-id",description = "댓글의 번호",required = true)
                                                 @PathVariable("reply-id")Integer replyId,
                                                 @ApiIgnore HttpSession session){

        Member member = (Member)session.getAttribute("member");

        if(member != null) {
            likeService.commentLikePlus(replyId,member);
            return new CommonResponse<>(HttpStatus.OK,"like plus");
        } else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST, ERRORCODE.LIKE_FAIL);
        }
    }

    @Operation(summary = "댓글 좋아요 -1", description = "가게 댓글에 좋아요를 누르면 좋아요를 줄인다.")
    @DeleteMapping(path = "/comment/minus/{reply-id}")
    public CommonResponse<?>minusCommentLike(@Parameter(name ="reply-id",description = "댓글의 번호",required = true)
                                             @PathVariable("reply-id")Integer replyId,
                                             @ApiIgnore HttpSession session){
        Member member = (Member)session.getAttribute("member");

        if(member != null) {
            likeService.commentLikeMinus(replyId,member);
            return new CommonResponse<>(HttpStatus.OK,"like minus");
        } else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST.value(),ERRORCODE.LIKE_FAIL);
        }
    }

    @Operation(summary = "댓글 좋아요 카운트", description = "로그인을 한 회원이 가게 댓글에 좋아요를 체크하는 기능")
    @GetMapping(path = "/comment/{reply-id}")
    public CommonResponse<List<String>>commentLikeCount(@Parameter(name ="reply-id",description = "댓글의 번호",required = true)
                                                        @PathVariable("reply-id")Integer replyId,
                                                        @ApiIgnore HttpSession session){
        Member member = (Member)session.getAttribute("member");

        if(member != null) {
            List<String>resultDate = likeService.likeCommentCount(replyId,member);
            return new CommonResponse<>(HttpStatus.OK,resultDate);
        }
        return new CommonResponse<>(HttpStatus.UNAUTHORIZED,ERRORCODE.NOT_AUTH);
    }
}

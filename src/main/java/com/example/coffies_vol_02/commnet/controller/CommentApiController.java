package com.example.coffies_vol_02.commnet.controller;

import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Api(tags = "Comment api",value = "댓글 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {
    private final CommentService commentService;

    @Operation(summary = "댓글 목록",description = "게시글 목록에서 댓글목록을 보여준다.")
    @GetMapping("/list/{board_id}")
    public CommonResponse<List<placeCommentResponseDto>>commentList(@PathVariable("board_id")Integer boardId){
        List<placeCommentResponseDto> list = new ArrayList<>();
        try{
            list = commentService.replyList(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "댓글 작성",description = "게시글 목록에서 댓글을 작성한다.")
    @PostMapping("/write/{board_id}")
    public CommonResponse<Integer>commentCreate(@PathVariable("board_id")Integer boardId, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CommentRequest dto){
        int WriteResult = 0;

        try{
            WriteResult = commentService.commentCreate(boardId,customUserDetails.getMember(),dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(), WriteResult);
    }

    @Operation(summary = "댓글 삭제",description = "게시글 목록에서 댓글을 삭제한다.")
    @DeleteMapping("/delete/{reply_id}")
    public CommonResponse<?>commentDelete(@PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            commentService.commentDelete(replyId,customUserDetails.getMember());
        }catch(Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }
    @Operation(summary = "가게 댓글 목록",description = "가게 조회화면에서 댓글목록을 보여준다.")
    @GetMapping("/place/list/{place_id}")
    public CommonResponse<List<placeCommentResponseDto>>placeCommentList(@PathVariable("place_id") Integer placeId){
        List<placeCommentResponseDto>commentResponseDtoList = new ArrayList<>();

        try{
            commentResponseDtoList = commentService.placeCommentList(placeId);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),commentResponseDtoList);
    }

    @Operation(summary = "댓글 목록",description = "게시글 목록에서 댓글목록을 보여준다.")
    @PostMapping("/place/write/{place_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>placeCommentWrite(@PathVariable("place_id") Integer placeId, @RequestBody CommentRequest dto, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        int insertResult = 0;

        try{
            insertResult = commentService.placeCommentCreate(placeId,dto,customUserDetails.getMember());
            //가게 평점 계산
            commentService.updateStar(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),insertResult);
    }

    @Operation(summary = "댓글 목록",description = "게시글 목록에서 댓글목록을 보여준다.")
    @DeleteMapping("/place/delete/{place_id}/{reply_id}")
    public CommonResponse<?>placeCommentDelete(@PathVariable("place_id")Integer placeId,@PathVariable("reply_id") Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try{
            commentService.placeCommentDelete(replyId,customUserDetails.getMember());
            //평점 계산
            commentService.updateStar(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete Comment");
    }
}

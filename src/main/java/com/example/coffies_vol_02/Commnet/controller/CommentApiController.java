package com.example.coffies_vol_02.Commnet.controller;

import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Commnet.service.CommentService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Api(tags = "comment api",value = "댓글 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {
    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록")
    @GetMapping("/list/{board_id}")
    public CommonResponse<List<CommentDto.CommentResponseDto>>commentList(@PathVariable("board_id")Integer boardId) throws Exception {
        List<CommentDto.CommentResponseDto> list = new ArrayList<>();

        list = commentService.replyList(boardId);

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @PostMapping("/write/{board_id}")
    public CommonResponse<Integer>commentWrite(@PathVariable("board_id")Integer boardId, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CommentDto.CommentRequestDto dto){
        int WriteResult = 0;

        WriteResult = commentService.replyWrite(boardId,customUserDetails.getMember(),dto);

        return new CommonResponse<>(HttpStatus.OK.value(), WriteResult);
    }

    @DeleteMapping("/delete/{reply_id}")
    public CommonResponse<?>commentDelete(@PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        commentService.commentDelete(replyId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }
    @ApiOperation(value = "가게 댓글 목록")
    @GetMapping("/placelist/{place_id}")
    public CommonResponse<List<CommentDto.CommentResponseDto>>placeCommentList(@PathVariable("place_id") Integer placeId) throws Exception {
        List<CommentDto.CommentResponseDto>commentResponseDtoList = new ArrayList<>();

        commentResponseDtoList = commentService.placeCommentList(placeId);

        return new CommonResponse<>(HttpStatus.OK.value(),commentResponseDtoList);
    }
    
    @ApiOperation("가게 댓글 작성")
    @PostMapping("/placewrite/{place_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>placeCommentWrite(@PathVariable("place_id") Integer placeId, @RequestBody CommentDto.CommentRequestDto dto,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {
        int insertResult = 0;

        insertResult = commentService.placeCommentWrite(placeId,dto,customUserDetails.getMember());
        //가게 평점 계산
        commentService.updateStar(placeId);

        return new CommonResponse<>(HttpStatus.OK.value(),insertResult);
    }

    @ApiOperation("가게 댓글 삭제")
    @DeleteMapping("/placedelete/{place_id}/{reply_id}")
    public CommonResponse<?>placeCommentDelete(@PathVariable("place_id")Integer placeId,@PathVariable("reply_id") Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {

        commentService.placeCommentDelete(replyId,customUserDetails.getMember());
        commentService.updateStar(placeId);

        return new CommonResponse<>(HttpStatus.OK.value(),"Delete Comment");
    }
}

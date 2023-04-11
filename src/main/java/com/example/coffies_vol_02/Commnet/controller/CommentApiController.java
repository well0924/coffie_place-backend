package com.example.coffies_vol_02.Commnet.controller;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Commnet.service.CommentService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Api(tags = "comment api",value = "댓글 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {
    private final CommentService commentService;

    @ApiOperation(value = "댓글 목록")
    @GetMapping("/list/{board_id}")
    public CommonResponse<List<CommentDto.CommentResponseDto>>commentList(@PathVariable("board_id")Integer boardId){
        List<CommentDto.CommentResponseDto> list = new ArrayList<>();

        try {
            list = commentService.replyList(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @PostMapping("/write/{board_id}")
    public CommonResponse<Integer>commentWrite(@PathVariable("board_id")Integer boardId, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CommentDto.CommentRequestDto dto){
        int WriteResult = 0;

        try {
            WriteResult = commentService.replyWrite(boardId,customUserDetails.getMember(),dto);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), WriteResult);
    }

    @DeleteMapping("/delete/{reply_id}")
    public CommonResponse<?>commentDelete(@PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        try {
            commentService.commentDelete(replyId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }

}

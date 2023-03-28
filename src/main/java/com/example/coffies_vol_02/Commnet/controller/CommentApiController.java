package com.example.coffies_vol_02.Commnet.controller;

import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.service.CommentService;
import com.example.coffies_vol_02.config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {
    private final CommentService commentService;

    @GetMapping("/list/{board_id}")
    public CommonResponse<?>commentList(@PathVariable("board_id")Integer boardId) throws Exception {
        List<CommentDto.CommentResponseDto> list = commentService.replyList(boardId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @PostMapping("/write/{board_id}")
    public CommonResponse<?>commentWrite(@PathVariable("board_id")Integer boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CommentDto.CommentRequestDto dto){
        int WriteResult = commentService.replyWrite(boardId,customUserDetails.getMember(),dto);
        return new CommonResponse<>(HttpStatus.OK.value(), WriteResult);
    }

    @DeleteMapping("/delete/{reply_id}")
    public CommonResponse<?>commentDelete(@PathVariable("reply_id")Integer replyId,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        commentService.commentDelete(replyId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }

}

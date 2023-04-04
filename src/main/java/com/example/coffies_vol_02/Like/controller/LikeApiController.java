package com.example.coffies_vol_02.Like.controller;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Like.service.LikeService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Api(tags = "Like api",value = "좋아요 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/like")
public class LikeApiController {
    private final LikeService likeService;

    @PostMapping("/plus")
    public CommonResponse<?>likePlus(Board board, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = likeService.createLikeBoard(board,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @PostMapping("/minus")
    public CommonResponse<?>likeMinus(Board board, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = likeService.removeLikeBoard(board,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @GetMapping("/duplicated/{board_id}")
    public CommonResponse<Boolean> likeDuplicated(@PathVariable(value = "board_id") Integer boardId,Board board,@AuthenticationPrincipal CustomUserDetails customUserDetails){

        boolean result = likeService.hasLikeBoard(board,customUserDetails.getMember());

        if(result == true){
            likeMinus(board,customUserDetails);
        }else{
            likePlus(board,customUserDetails);
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

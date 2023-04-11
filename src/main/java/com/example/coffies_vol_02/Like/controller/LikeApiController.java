package com.example.coffies_vol_02.Like.controller;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Like.service.LikeService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

@Api(tags = "Like Api Controller",value = "좋아요 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/like")
public class LikeApiController {
    private final LikeService likeService;

    @ApiOperation(value = "좋아요 +1")
    @PostMapping("/plus")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<String>likePlus(Board board,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = "";

        try {
            result = likeService.createLikeBoard(board,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }
    @ApiOperation(value = "좋아요 -1")
    @PostMapping("/minus")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<String>likeMinus(Board board, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        String result = "";

        try {
            result = likeService.removeLikeBoard(board,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @GetMapping("/duplicated/{board_id}")
    public CommonResponse<Boolean> likeDuplicated(@PathVariable(value = "board_id") Integer boardId,Board board,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        boolean result = likeService.hasLikeBoard(boardId,board,customUserDetails.getMember());

        try {
            if(result){
                likeMinus(board,customUserDetails);
            }else{
                likePlus(board,customUserDetails);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

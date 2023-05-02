package com.example.coffies_vol_02.FavoritePlace.controller.api;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.FavoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.FavoritePlace.service.FavoritePlaceService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class FavoriteApiController {
    private FavoritePlaceService favoritePlaceService;

    @ApiOperation("위시리스트 목록")
    @GetMapping(path = "/{user_id}")
    public CommonResponse<List<FavoritePlaceDto.FavoriteResponseDto>>MyWishList(@PathVariable("user_id")String userId,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        List<FavoritePlaceDto.FavoriteResponseDto>list= favoritePlaceService.findByMemberId(userId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation("위시리스트 중복 체크")
    @GetMapping(path = "/check/{member_id}/{place_id}")
    public CommonResponse<?>wishListCheck(@PathVariable("member_id")Integer memberId, @PathVariable("place_id") Integer placeId,Integer wishId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        boolean checkResult = favoritePlaceService.hasWishPlace(placeId,memberId);
        if(checkResult == false){
            wishListAdd(memberId,placeId,customUserDetails);
        }else if(checkResult == true){//checkResult 가 true인 경우
            wishListDelete(wishId);
        }
        return new CommonResponse<>(HttpStatus.OK.value(),checkResult);
    }

    @ApiOperation("가게 위시리스트에 추가")
    @PostMapping(path = "/{member_id}/{place_id}")
    public CommonResponse<?>wishListAdd(@PathVariable("member_id")Integer memberId, @PathVariable("place_id") Integer placeId, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        favoritePlaceService.wishListAdd(memberId,placeId);
        return new CommonResponse<>(HttpStatus.OK.value(),"wishList Add");
    }

    @ApiOperation("위시리스트 삭제")
    @DeleteMapping(path = "/{wish_id}")
    public CommonResponse<String>wishListDelete(@PathVariable("wish_id")Integer wishId){
        favoritePlaceService.wishDelete(wishId);
        return new CommonResponse<>(HttpStatus.OK.value(),"wishlist delete!");
    }

    @ApiOperation(value = "로그인한 회원이 작성한 글")
    @GetMapping(path = "/contents/{id}")
    public CommonResponse<Page<BoardDto.BoardResponseDto>>MyArticle(@PathVariable("id") String userId, @AuthenticationPrincipal CustomUserDetails customUserDetails, @PageableDefault Pageable pageable){
        Page<BoardDto.BoardResponseDto> list = favoritePlaceService.getMyPageBoardList(pageable,customUserDetails.getMember(),userId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "로그인한 회원이 작성한 댓글")
    @GetMapping(path = "/comment/{id}")
    public CommonResponse<List<CommentDto.CommentResponseDto>>MyComment(@PathVariable("id") String userId,@AuthenticationPrincipal CustomUserDetails customUserDetails,Pageable pageable){
        List<CommentDto.CommentResponseDto>list = favoritePlaceService.getMyPageCommnetList(userId,pageable,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}

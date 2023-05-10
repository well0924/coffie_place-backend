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
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class FavoriteApiController {
    private FavoritePlaceService favoritePlaceService;

    @ApiOperation("위시리스트 목록")
    @GetMapping(path = "/{user_id}")
    public CommonResponse<List<FavoritePlaceDto.FavoriteResponseDto>>MyWishList(@PathVariable("user_id")String userId){
        List<FavoritePlaceDto.FavoriteResponseDto>list= new ArrayList<>();
        try{
            list= favoritePlaceService.findByMemberId(userId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation("위시리스트 중복 체크")
    @GetMapping(path = "/check/{member_id}/{place_id}")
    public CommonResponse<?>wishListCheck(@PathVariable("member_id")String memberId, @PathVariable("place_id") Integer placeId,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        boolean checkResult = false;
        try{
            checkResult = favoritePlaceService.hasWishPlace(placeId,customUserDetails.getMember().getId());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),checkResult);
    }

    @ApiOperation("가게 위시리스트에 추가")
    @PostMapping(path = "/{member_id}/{place_id}")
    public CommonResponse<?>wishListAdd(@PathVariable("member_id")Integer memberId, @PathVariable("place_id") Integer placeId){
        try{
            favoritePlaceService.wishListAdd(memberId,placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishList Add");
    }

    @ApiOperation("위시리스트 삭제")
    @DeleteMapping(path = "/delete/{place_id}")
    public CommonResponse<String>wishListDelete(@PathVariable("place_id")Integer placeId){
        try{
            favoritePlaceService.wishDelete(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishlist delete!");
    }

    @ApiOperation(value = "로그인한 회원이 작성한 글")
    @GetMapping(path = "/contents/{id}")
    public CommonResponse<Page<BoardDto.BoardResponseDto>>MyArticle(@PathVariable("id") String userId, @PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<BoardDto.BoardResponseDto> list = null;
        try{
            list = favoritePlaceService.getMyPageBoardList(pageable, userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "로그인한 회원이 작성한 댓글")
    @GetMapping(path = "/comment/{id}")
    public CommonResponse<List<CommentDto.CommentResponseDto>>MyComment(@PathVariable("id") String userId, Pageable pageable){
        List<CommentDto.CommentResponseDto>list = new ArrayList<>();
        try{
            list = favoritePlaceService.getMyPageCommnetList(userId,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}

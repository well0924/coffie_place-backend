package com.example.coffies_vol_02.FavoritePlace.controller.api;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.FavoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.FavoritePlace.service.FavoritePlaceService;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class FavoriteApiController {
    private FavoritePlaceService favoritePlaceService;

    @Operation(summary = "위시리스트 목록",description = "가게조회 페이지에서 위시리스트를 추가한 목록을 마이페이지에서 볼 수 있다.")
    @GetMapping(path = "/{user_id}")
    public CommonResponse<Page<FavoritePlaceDto.FavoriteResponseDto>>MyWishList(@ApiIgnore @PageableDefault(size = 5,sort = "id",direction= Sort.Direction.DESC) Pageable pageable, @PathVariable("user_id")String userId){
        Page<FavoritePlaceDto.FavoriteResponseDto>list= null;

        try{
            list = favoritePlaceService.MyWishList(pageable,userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "위시리스트 중복 체크",description = "가게조회 페이지에서 위시리스트를 눌렀을때 중복체크를 한다.")
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

    @Operation(summary = "가게 위시리스트에 추가",description = "가게 조회페이지에서 위시리스트 추가를 누르면 위시리스트가 추가가 된다.")
    @PostMapping(path = "/{member_id}/{place_id}")
    public CommonResponse<?>wishListAdd(@PathVariable("member_id")Integer memberId, @PathVariable("place_id") Integer placeId){
        try{
            favoritePlaceService.wishListAdd(memberId,placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishList Add");
    }

    @Operation(summary = "위시리스트 삭제",description = "마이페이지에서 위시리스트에 있는 위시리스트를 삭제한다.")
    @DeleteMapping(path = "/delete/{place_id}")
    public CommonResponse<String>wishListDelete(@PathVariable("place_id")Integer placeId){
        try{
            favoritePlaceService.wishDelete(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishlist delete!");
    }

    @Operation(summary = "로그인한 회원이 작성한 글",description = "마이페이지에서 로그인한 회원이 작성한 글의 목록을 보여준다.")
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
    
    @Operation(summary = "로그인한 회원이 작성한 댓글",description = "마이페이지에서 로그인한 회원이 작성한 댓글의 목록을 보여준다.")
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

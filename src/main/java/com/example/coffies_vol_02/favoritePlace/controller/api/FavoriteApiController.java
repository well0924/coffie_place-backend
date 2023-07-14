package com.example.coffies_vol_02.favoritePlace.controller.api;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
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

@Api(tags = "Favorite api",value = "마이 페이지 관련 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/mypage")
public class FavoriteApiController {
    private FavoritePlaceService favoritePlaceService;

    @ApiOperation(value = "위시리스트 목록", notes = "가게조회 페이지에서 위시리스트를 추가한 목록을 마이페이지에서 볼 수 있다.")
    @ApiImplicitParam(name = "회원 아이디",value = "user_id",dataType = "String",required = true)
    @GetMapping(path = "/{user_id}")
    public CommonResponse<Page<FavoritePlaceResponseDto>>MyWishList(@ApiIgnore @PageableDefault(size = 5,sort = "id",direction= Sort.Direction.DESC) Pageable pageable, 
                                                                    @PathVariable("user_id")String userId){
        Page<FavoritePlaceResponseDto>list= null;

        try{
            list = favoritePlaceService.MyWishList(pageable,userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "위시리스트 중복 체크",notes = "가게조회 페이지에서 위시리스트를 눌렀을때 중복체크를 한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "member_id",name = "회원 아이디",dataType = "String",required = true),
            @ApiImplicitParam(value = "place_id",name = "가게 번호",dataType = "Integer",required = true)})
    @GetMapping(path = "/check/{member_id}/{place_id}")
    public CommonResponse<?>wishListCheck(@PathVariable("member_id")String memberId, @PathVariable("place_id") Integer placeId,
                                          @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        boolean checkResult = false;

        try{
            checkResult = favoritePlaceService.hasWishPlace(placeId,customUserDetails.getMember().getId());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),checkResult);
    }

    @ApiOperation(value = "가게 위시리스트에 추가", notes = "가게 조회페이지에서 위시리스트 추가를 누르면 위시리스트가 추가가 된다")
    @ApiImplicitParams({
            @ApiImplicitParam(value = "member_id",name = "회원 번호",dataType = "Integer",required = true),
            @ApiImplicitParam(value = "place_id",name = "가게 번호",dataType = "Integer",required = true)})
    @PostMapping(path = "/{member_id}/{place_id}")
    public CommonResponse<?>wishListAdd(@PathVariable("member_id")Integer memberId, @PathVariable("place_id") Integer placeId){
        try{
            favoritePlaceService.wishListAdd(memberId,placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishList Add");
    }

    @ApiOperation(value = "위시리스트 삭제",notes = "마이페이지에서 위시리스트에 있는 위시리스트를 삭제한다.")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "가게번호",value = "place_id",dataType = "Integer",required = true)})
    @DeleteMapping(path = "/delete/{place_id}")
    public CommonResponse<String>wishListDelete(@PathVariable("place_id")Integer placeId){
        try{
            favoritePlaceService.wishDelete(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishlist delete!");
    }

    @ApiOperation(value = "로그인한 회원이 작성한 글", notes = "마이페이지에서 로그인한 회원이 작성한 글의 목록을 보여준다.")
    @ApiImplicitParams({@ApiImplicitParam(name = "회원 아이디",value = "id",dataType = "String",required = true)})
    @GetMapping(path = "/contents/{id}")
    public CommonResponse<Page<BoardResponse>>MyArticle(@PathVariable("id") String userId,@ApiIgnore @PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<BoardResponse> list = null;
        try{
            list = favoritePlaceService.getMyPageBoardList(pageable, userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "로그인한 회원이 작성한 댓글",notes = "마이페이지에서 로그인한 회원이 작성한 댓글의 목록을 보여준다.")
    @ApiImplicitParams({@ApiImplicitParam(name = "회원 아이디",value = "user_id",dataType = "String",required = true)})
    @GetMapping(path = "/comment/{id}")
    public CommonResponse<List<placeCommentResponseDto>>MyComment(@PathVariable("id") String userId,@ApiIgnore @PageableDefault Pageable pageable){
        List<placeCommentResponseDto>list = new ArrayList<>();
        try{
            list = favoritePlaceService.getMyPageCommnetList(userId,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}

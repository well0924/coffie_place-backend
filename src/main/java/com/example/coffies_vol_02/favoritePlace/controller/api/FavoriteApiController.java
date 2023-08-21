package com.example.coffies_vol_02.favoritePlace.controller.api;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.redis.RedisService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import com.example.coffies_vol_02.favoritePlace.domain.dto.recentPostDto;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
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

    private RedisService redisService;

    @ApiOperation(value = "위시리스트 목록", notes = "가게조회 페이지에서 위시리스트를 추가한 목록을 마이페이지에서 볼 수 있다.")
    @GetMapping(path = "/{user_id}")
    public CommonResponse<Page<FavoritePlaceResponseDto>>MyWishList(@ApiIgnore
                                                                    @PageableDefault(size = 5,sort = "id",direction= Sort.Direction.DESC) Pageable pageable,
                                                                    @Parameter(name = "user_id",description = "회원의 아이디",required = true)
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
    @GetMapping(path = "/check/{member_id}/{place_id}")
    public CommonResponse<?>wishListCheck(@Parameter(name = "member_id",description = "회원 아이디")
                                          @PathVariable("member_id")String memberId,
                                          @Parameter(name = "place_id",description = "가게 번호")
                                          @PathVariable("place_id") Integer placeId,
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
    @PostMapping(path = "/{member_id}/{place_id}")
    public CommonResponse<?>wishListAdd(@Parameter(name = "member_id",description = "회원의 번호",required = true)
                                        @PathVariable("member_id")Integer memberId,
                                        @Parameter(name = "place_id",description = "가게의 번호",required = true)
                                        @PathVariable("place_id") Integer placeId){
        try{
            favoritePlaceService.wishListAdd(memberId,placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishList Add");
    }

    @ApiOperation(value = "위시리스트 삭제",notes = "마이페이지에서 위시리스트에 있는 위시리스트를 삭제한다.")
    @DeleteMapping(path = "/delete/{place_id}")
    public CommonResponse<String>wishListDelete(@Parameter(name = "place_id",description = "가게의 번호",required = true)
                                                @PathVariable("place_id")Integer placeId){
        try{
            favoritePlaceService.wishDelete(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"wishlist delete!");
    }

    @ApiOperation(value = "로그인한 회원이 작성한 글", notes = "마이페이지에서 로그인한 회원이 작성한 글의 목록을 보여준다.")
    @GetMapping(path = "/contents/{id}")
    public CommonResponse<Page<BoardResponse>>MyArticle(@Parameter(name = "id",description = "회원의 아이디",required = true)
                                                        @PathVariable("id") String userId,
                                                        @ApiIgnore
                                                        @PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        Page<BoardResponse> list = null;
        try{
            list = favoritePlaceService.getMyPageBoardList(pageable, userId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "로그인한 회원이 작성한 댓글",notes = "마이페이지에서 로그인한 회원이 작성한 댓글의 목록을 보여준다.")
    @GetMapping(path = "/comment/{id}")
    public CommonResponse<List<placeCommentResponseDto>>MyComment(@Parameter(name = "id",description = "회원의 아이디",required = true)
                                                                  @PathVariable("id") String userId,
                                                                  @ApiIgnore @PageableDefault Pageable pageable){

        List<placeCommentResponseDto>list = new ArrayList<>();

        try{
            list = favoritePlaceService.getMyPageCommnetList(userId,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "회원 위치에서 가까운 가게 조회", notes = "회원의 위경도를 기준으로 해서 가까운 가게 목록을 조회한다")
    @GetMapping(path = "/nearlist")
    public CommonResponse<?> placeNearList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {
        List<PlaceResponseDto> nearList = new ArrayList<>();

        try {
            nearList = favoritePlaceService.placeNear(customUserDetails.getMember().getMemberLat(),customUserDetails.getMember().getMemberLng());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),nearList);
    }

    @ApiOperation(value = "회원이 자유게시글에서 조회한 글 확인하는 기능")
    @GetMapping("/recent/{user-idx}")
    public CommonResponse<?>recentPostList(@PathVariable("user-idx") Integer userIdx){
        List<recentPostDto>recentPostDtoList = new ArrayList<>();

        try{
            recentPostDtoList = redisService.recentPostDtoList(userIdx);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),recentPostDtoList);
    }

    @ApiOperation(value ="회원이 좋아요를 한 게시글 목록을 확인하는 기능")
    @GetMapping("/like/{user-id}")
    public CommonResponse<?>likeBoardList(@PathVariable("user-id")String userId,@AuthenticationPrincipal CustomUserDetails customUserDetails,Pageable pageable){

        Page<BoardResponse>result = null;

        try{
            result = favoritePlaceService.likedBoardList(customUserDetails.getMember().getId(),pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }
}

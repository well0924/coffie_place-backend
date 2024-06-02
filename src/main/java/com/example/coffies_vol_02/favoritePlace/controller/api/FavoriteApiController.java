package com.example.coffies_vol_02.favoritePlace.controller.api;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;
import java.util.List;

@Api(tags = "Favorite api",value = "마이 페이지 관련 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/my-page")
public class FavoriteApiController {

    private final FavoritePlaceService favoritePlaceService;

    @Operation(summary = "위시리스트 목록",description = "가게조회 페이지에서 위시리스트를 추가한 목록을 마이페이지에서 볼 수 있다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = FavoritePlaceResponseDto.class)))
    })
    @GetMapping(path = "/{user-id}")
    public CommonResponse<Page<FavoritePlaceResponseDto>>MyWishList(@ApiIgnore
                                                                    @PageableDefault(size = 5,sort = "id",direction= Sort.Direction.DESC) Pageable pageable,
                                                                    @Parameter(name = "user-id",description = "회원의 아이디",required = true)
                                                                    @PathVariable("user-id")String userId){

        Page<FavoritePlaceResponseDto>list= favoritePlaceService.MyWishList(pageable,userId);

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "위시리스트 중복 체크",description = "가게조회 페이지에서 위시리스트를 눌렀을때 중복체크를 한다.")
    @GetMapping(path = "/{member-id}/{place-id}")
    public CommonResponse<?>wishListCheck(@Parameter(name = "member-id",description = "회원 아이디")
                                          @PathVariable("member-id")String memberId,
                                          @Parameter(name = "place-id",description = "가게 번호")
                                          @PathVariable("place-id") Integer placeId,
                                          @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        boolean checkResult = favoritePlaceService.hasWishPlace(placeId,customUserDetails.getMember().getId());

        return new CommonResponse<>(HttpStatus.OK.value(),checkResult);
    }

    @Operation(summary = "가게 위시리스트에 추가", description = "가게 조회페이지에서 위시리스트 추가를 누르면 위시리스트가 추가가 된다")
    @PostMapping(path = "/{member-id}/{place-id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>wishListAdd(@Parameter(name = "member-id",description = "회원의 번호",required = true)
                                        @PathVariable("member-id")Integer memberId,
                                        @Parameter(name = "place-id",description = "가게의 번호",required = true)
                                        @PathVariable("place-id") Integer placeId){

        favoritePlaceService.wishListAdd(memberId,placeId);

        return new CommonResponse<>(HttpStatus.OK.value(),"wishList Add");
    }

    @Operation(summary = "위시리스트 삭제",description = "마이페이지에서 위시리스트에 있는 위시리스트를 삭제한다.")
    @DeleteMapping(path = "/{place-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<String>wishListDelete(@Parameter(name = "place-id",description = "가게의 번호",required = true)
                                                @PathVariable("place-id")Integer placeId){

        favoritePlaceService.wishDelete(placeId);

        return new CommonResponse<>(HttpStatus.OK.value(),"wishlist delete!");
    }

    @Operation(summary = "로그인한 회원이 작성한 글", description = "마이페이지에서 로그인한 회원이 작성한 글의 목록을 보여준다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping(path = "/contents/{id}")
    public CommonResponse<Page<BoardResponse>>MyArticle(@Parameter(name = "id",description = "회원의 아이디",required = true)
                                                        @PathVariable("id") String userId,
                                                        @ApiIgnore
                                                        @PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){

        Page<BoardResponse> list = favoritePlaceService.getMyPageBoardList(pageable, userId);

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @Operation(summary = "로그인한 회원이 작성한 댓글",description = "마이페이지에서 로그인한 회원이 작성한 댓글의 목록을 보여준다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = placeCommentResponseDto.class)))
    })
    @GetMapping(path = "/comment/{id}")
    public CommonResponse<List<placeCommentResponseDto>>MyComment(@Parameter(name = "id",description = "회원의 아이디",required = true)
                                                                  @PathVariable("id") String userId,
                                                                  @ApiIgnore @PageableDefault Pageable pageable){

        List<placeCommentResponseDto>list = favoritePlaceService.getMyPageCommnetList(userId,pageable);

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "회원 위치에서 가까운 가게 조회",description = "회원의 위경도를 기준으로 해서 가까운 가게 목록을 조회한다",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = PlaceResponseDto.class)))
    })
    @GetMapping(path = "/near-list")
    public CommonResponse<?> placeNearList(@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        List<PlaceResponseDto> nearList = favoritePlaceService.placeNear(customUserDetails.getMember().getMemberLat(),customUserDetails.getMember().getMemberLng());

        return new CommonResponse<>(HttpStatus.OK.value(),nearList);
    }

    @Operation(summary="회원이 좋아요를 한 게시글 목록을 확인하는 기능",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping("/like/{user-id}")
    public CommonResponse<?>likeBoardList(@Parameter(name = "id",description = "회원의 아이디",required = true)
                                          @PathVariable("user-id")String userId,
                                          @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails,Pageable pageable){

        Page<BoardResponse>result = favoritePlaceService.likedBoardList(customUserDetails.getMember().getId(),pageable);

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

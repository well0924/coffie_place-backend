package com.example.coffies_vol_02.commnet.controller;

import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.List;

@Log4j2
@Api(tags = "Comment api",value = "댓글 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {

    private final CommentService commentService;

    @Operation(summary = "댓글 목록", description = "게시글 목록에서 댓글목록을 보여준다.",responses = {
            @ApiResponse(responseCode = "200",description = "정상적인 응답",content = @Content(schema =@Schema(implementation = placeCommentResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "댓글이 없는 경우",content = @Content(schema =@Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/{board_id}")
    public CommonResponse<List<placeCommentResponseDto>>listComment(@Parameter(name = "board_id",description = "게시글의 번호",required = true)
                                                                    @PathVariable("board_id")Integer boardId){
        List<placeCommentResponseDto> list = new ArrayList<>();

        try{
            list = commentService.freeBoardCommentList(boardId);

            if(list.isEmpty()){
                return new CommonResponse<>(HttpStatus.OK,ERRORCODE.NOT_REPLY);
            }
        }catch (Exception e){
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,list);
        }
        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "댓글 작성", description = "게시글 목록에서 댓글을 작성한다.", responses = {
            @ApiResponse(responseCode = "201", description = "정상적으로 댓글을 작성하는 경우")
    })
    @PostMapping("/{board_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>createComment(@Parameter(name = "board_id",description = "게시글의 번호",required = true)
                                                @PathVariable("board_id")Integer boardId,
                                                @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                @RequestBody CommentRequest dto){

        Integer writeResult = commentService.createFreeBoardComment(boardId,customUserDetails.getMember(),dto);

        if(writeResult !=null && writeResult > 0){
            return new CommonResponse<>(HttpStatus.OK,writeResult);
        }else{
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.REPLY_FAIL);
        }
    }

    @Operation(summary = "댓글 삭제", description = "게시글 목록에서 댓글을 삭제한다.")
    @DeleteMapping("/{reply_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?>deleteComment(@Parameter(name = "board_id",description = "게시글의 번호",required = true) @PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        commentService.deleteFreeBoardComment(replyId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK, "Delete O.k");
    }

    @Operation(summary = "가게 댓글 목록", description = "가게 조회화면에서 댓글목록을 보여준다.",responses={
            @ApiResponse(responseCode = "200",description = "가게 댓글의 목록을 정상적으로 보여준다.", content = @Content(mediaType = "application/json",schema = @Schema(implementation = placeCommentResponseDto.class)))
    })
    @GetMapping("/place/list/{place_id}")
    public CommonResponse<List<placeCommentResponseDto>>listPlaceComment(@Parameter(name = "place_id",description = "가게의 번호",required = true) @PathVariable("place_id") Integer placeId)throws Exception{

        List<placeCommentResponseDto>commentResponseDtoList = commentService.placeCommentList(placeId);

        return new CommonResponse<>(HttpStatus.OK,commentResponseDtoList);
    }

    @Operation(summary = "가게댓글 작성", description = "게시글 목록에서 댓글을 작성한다.")
    @PostMapping("/place/write/{place_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>createPlaceComment(@Parameter(name = "place_id",description = "가게의 번호",required = true)
                                                    @PathVariable("place_id") Integer placeId,
                                                    @RequestBody CommentRequest dto,
                                                    @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception{

        Integer insertResult = commentService.createPlaceComment(placeId,dto,customUserDetails.getMember());

        //가게 평점 계산
        commentService.updateStar(placeId);

        return new CommonResponse<>(HttpStatus.OK,insertResult);
    }

    @Operation(summary = "댓글 삭제",description = "게시글 목록에서 댓글을 삭제한다.")
    @DeleteMapping("/place/delete/{place_id}/{reply_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?>deletePlaceComment(@Parameter(name = "place_id",description = "가게의 번호",required = true)
                                               @PathVariable("place_id")Integer placeId,
                                               @Parameter(name = "reply_id",description = "댓글의 번호",required = true)
                                               @PathVariable("reply_id") Integer replyId,
                                               @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails)throws Exception {

        commentService.deletePlaceComment(replyId,customUserDetails.getMember());
        //평점 계산
        commentService.updateStar(placeId);

        return new CommonResponse<>(HttpStatus.OK,"Delete Comment");
    }

    @Operation(summary = "최근에 작성한 댓글",description = "게시판 댓글과 가게 댓글에서 작성일 순으로 5개를 출력")
    @GetMapping("/recent-reply")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<placeCommentResponseDto>>CommentListTop5(){

        List<placeCommentResponseDto>result = commentService.recentCommentTop5();

        return new CommonResponse<>(HttpStatus.OK,result);
    }
}

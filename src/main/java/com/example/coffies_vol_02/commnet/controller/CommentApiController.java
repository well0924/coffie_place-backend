package com.example.coffies_vol_02.commnet.controller;

import com.example.coffies_vol_02.commnet.domain.dto.request.CommentRequest;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.service.CommentService;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.exception.Dto.ErrorDto;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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
@Api(tags = "Comment api",description = "댓글 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/comment")
public class CommentApiController {
    private final CommentService commentService;

    @Operation(summary = "댓글 목록", description = "게시글 목록에서 댓글목록을 보여준다.",responses = {
            @ApiResponse(responseCode = "200",description = "정상적인 응답",content = @Content(schema =@Schema(implementation = placeCommentResponseDto.class))),
            @ApiResponse(responseCode = "404",description = "댓글이 없는 경우",content = @Content(schema =@Schema(implementation = CommonResponse.class)))
    })
    @GetMapping("/list/{board_id}")
    public CommonResponse<List<placeCommentResponseDto>>commentList(@Parameter(name = "board_id",description = "게시글의 번호",required = true) @PathVariable("board_id")Integer boardId){
        List<placeCommentResponseDto> list = new ArrayList<>();
        try{
            list = commentService.replyList(boardId);

            if(list.isEmpty()){
                return new CommonResponse<>(ERRORCODE.NOT_REPLY.getErrorCode(),list);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),list);
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "댓글 작성", notes = "게시글 목록에서 댓글을 작성한다.")
    @PostMapping("/write/{board_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>commentCreate(@Parameter(name = "board_id",description = "게시글의 번호",required = true) @PathVariable("board_id")Integer boardId, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails, @RequestBody CommentRequest dto){
        int WriteResult = 0;

        try{
            WriteResult = commentService.commentCreate(boardId,customUserDetails.getMember(),dto);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(), WriteResult);
    }

    @ApiOperation(value = "댓글 삭제", notes = "게시글 목록에서 댓글을 삭제한다.")
    @DeleteMapping("/delete/{reply_id}")
    public CommonResponse<?>commentDelete(@Parameter(name = "board_id",description = "게시글의 번호",required = true) @PathVariable("reply_id")Integer replyId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        try{
            commentService.commentDelete(replyId,customUserDetails.getMember());
        }catch(Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(), "Delete O.k");
    }
    @ApiOperation(value = "가게 댓글 목록", notes = "가게 조회화면에서 댓글목록을 보여준다.")
    @GetMapping("/place/list/{place_id}")
    public CommonResponse<List<placeCommentResponseDto>>placeCommentList(@Parameter(name = "place_id",description = "가게의 번호",required = true) @PathVariable("place_id") Integer placeId){
        List<placeCommentResponseDto>commentResponseDtoList = new ArrayList<>();

        try{
            commentResponseDtoList = commentService.placeCommentList(placeId);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),commentResponseDtoList);
    }

    @ApiOperation(value = "가게댓글 작성", notes = "게시글 목록에서 댓글을 작성한다.")
    @PostMapping("/place/write/{place_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>placeCommentWrite(@Parameter(name = "place_id",description = "가게의 번호",required = true)
                                                        @PathVariable("place_id") Integer placeId,
                                                    @RequestBody CommentRequest dto,
                                                    @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        int insertResult = 0;

        try{
            insertResult = commentService.placeCommentCreate(placeId,dto,customUserDetails.getMember());
            //가게 평점 계산
            commentService.updateStar(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),insertResult);
    }

    @ApiOperation(value = "댓글 삭제",notes = "게시글 목록에서 댓글을 삭제한다.")
    @DeleteMapping("/place/delete/{place_id}/{reply_id}")
    public CommonResponse<?>placeCommentDelete(@Parameter(name = "place_id",description = "가게의 번호",required = true) @PathVariable("place_id")Integer placeId,
                                               @Parameter(name = "reply_id",description = "댓글의 번호",required = true) @PathVariable("reply_id") Integer replyId,
                                               @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) {

        try{
            commentService.placeCommentDelete(replyId,customUserDetails.getMember());
            //평점 계산
            commentService.updateStar(placeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete Comment");
    }

    @Operation(summary = "최근에 작성한 댓글",description = "게시판 댓글과 가게 댓글에서 작성일 순으로 5개를 출력")
    public CommonResponse<List<placeCommentResponseDto>>recentCommentListTop5(){
        List<placeCommentResponseDto>result = new ArrayList<>();
        try {
            result = commentService.recentCommentTop5();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }
}

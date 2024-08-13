package com.example.coffies_vol_02.notice.controller.api;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Notice Api Controller",value = "공지게시판 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {

    private final NoticeService noticeService;

    @Operation(summary = "공지 게시판 목록", description = "공지게시판 페이지에서 목록을 보여준다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = NoticeResponse.class)))
    })
    @GetMapping(value = "/")
    public CommonResponse<Page<NoticeResponse>>noticeBoard(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){

        Page<NoticeResponse> list = noticeService.listNoticeBoard(pageable);

        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "공지 게시판 검색",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = NoticeResponse.class)))
    })
    @GetMapping("/search")
    public CommonResponse<?>searchNoticeBoard(
            @Parameter(name = "searchType",description = "검색에 필요한 타입",example = "t(제목)")
            @RequestParam(value = "searchType",required = false) String searchType,
            @Parameter(name = "searchVal",description = "검색어")
            @RequestParam(value = "searchVal") String searchVal,
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){

        Page<NoticeResponse> list = noticeService.searchNoticeBoard(SearchType.valueOf(searchType),searchVal,pageable);

        //검색시 결과가 없는 경우
        if(StringUtils.isBlank(searchVal)||StringUtils.isBlank(searchType)){
            return new CommonResponse<>(HttpStatus.OK, ERRORCODE.NOT_SEARCH_VALUE);
        }

        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "공지게시글 조회",description = "공지게시글을 단일 조회한다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = NoticeResponse.class)))
    })
    @GetMapping("/{notice-id}")
    public CommonResponse<NoticeResponse>findNoticeBoardById(@Parameter(name = "notice-id",description = "공지게시글 번호",required = true)
                                                             @PathVariable("notice-id")Integer noticeId){

        NoticeResponse detail = noticeService.findNoticeBoardById(noticeId);

        return new CommonResponse<>(HttpStatus.OK,detail);
    }

    @Operation(summary = "공지게시글 작성", description = "공지게시글 작성화면에서 게시글을 작성한다.")
    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>createNoticeBoard(@Parameter(name = "noticeDto",description = "공지게시글 요청 dto",required = true)
                                                @Valid @RequestPart(value = "noticeDto") NoticeRequest dto,
                                                @Parameter(name = "files",description = "공지게시글 첨부파일")
                                                @RequestPart(value = "files",required = false) List<MultipartFile> files ,
                                                BindingResult bindingResult)throws Exception{

        Integer insertResult = noticeService.createNoticeBoard(dto,files);

        if(insertResult > 0){
            return new CommonResponse<>(HttpStatus.CREATED,insertResult);
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()){
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.BOARD_FAIL);
        }else {
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,ERRORCODE.INTERNAL_SERVER_ERROR);
        }
    }

    @Operation(summary = "공지게시글 수정", description = "공지게시글 수정화면에서 게시글을 수정한다.",responses = {
            @ApiResponse(responseCode = "201",description = "정상적으로 게시글을 수정하는 경우")
    })
    @PatchMapping(value = "/{notice-id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Integer>updateNoticeBoard(@Parameter(name = "notice-id",description = "공지게시글 번호",required = true)
                                               @PathVariable("notice-id")Integer noticeId,
                                               @Parameter(name = "updateDto",description = "공지게시글 수정 dto")
                                               @RequestPart(value = "updateDto") NoticeRequest dto,
                                               @Parameter(name = "files",description = "공지게시글 첨부파일")
                                               @RequestPart(value = "files",required = false) List<MultipartFile>files)throws Exception{

        Integer updateResult = noticeService.updateNoticeBoard(noticeId,dto,files);

        if(updateResult>0){
            return new CommonResponse<>(HttpStatus.OK.value(),updateResult);
        }else if(HttpStatus.BAD_REQUEST.is4xxClientError()){
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.NOTICE_FAIL);
        }else{
            return new CommonResponse<>(HttpStatus.INTERNAL_SERVER_ERROR,ERRORCODE.INTERNAL_SERVER_ERROR);
        }
    }
    
    @Operation(summary = "공지게시글 삭제", description = "공지게시글 수정화면에서 게시글을 삭제한다.")
    @DeleteMapping("/{notice-id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?>deleteNoticeBoard(@Parameter(name = "notice-id",description = "공지게시글 번호",required = true)
                                         @PathVariable("notice-id")Integer noticeId)throws Exception{

        noticeService.deleteNoticeBoard(noticeId);

        return new CommonResponse<>(HttpStatus.NO_CONTENT,"Delete O.k");
    }
}

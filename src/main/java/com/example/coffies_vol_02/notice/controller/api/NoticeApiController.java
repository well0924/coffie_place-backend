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
    @GetMapping("/list")
    public CommonResponse<Page<NoticeResponse>>noticeList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<NoticeResponse> list = null;

        try{
            list = noticeService.noticeAllList(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "공지 게시판 검색",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = NoticeResponse.class)))
    })
    @GetMapping("/search")
    public CommonResponse<?>noticeSearchList(
            @Parameter(name = "searchType",description = "검색에 필요한 타입",example = "t(제목)",required = true)
            @RequestParam(value = "searchType",required = true) SearchType searchType,
            @Parameter(name = "searchVal",description = "검색어",required = false)
            @RequestParam(value = "searchVal",required = false) String searchVal,
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){

        Page<NoticeResponse> list = null;
        //검색시 결과가 없는 경우
        if(searchVal==null||searchVal==""||searchType.getValue().isEmpty()||searchType.getValue()==""){
            return new CommonResponse<>(HttpStatus.OK.value(), ERRORCODE.NOT_SEARCH_VALUE.getMessage());
        }

        try{
            list = noticeService.noticeSearchAll(searchType,searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "공지게시글 조회",description = "공지게시글을 단일 조회한다.",responses = {
            @ApiResponse(responseCode = "200",content = @Content(mediaType = "application/json",schema = @Schema(implementation = NoticeResponse.class)))
    })
    @GetMapping("/detail/{notice_id}")
    public CommonResponse<NoticeResponse>noticeDetail(@Parameter(name = "notice_id",description = "공지게시글 번호",required = true)
                                                      @PathVariable("notice_id")Integer noticeId){
        NoticeResponse detail = noticeService.findNotice(noticeId);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "공지게시글 작성", description = "공지게시글 작성화면에서 게시글을 작성한다.")
    @PostMapping(value = "/write", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>noticeWrite(  @Parameter(name = "noticeDto",description = "공지게시글 요청 dto",required = true)
                                                @Valid @RequestPart(value = "noticeDto",required = true) NoticeRequest dto,
                                                @Parameter(name = "files",description = "공지게시글 첨부파일",required = false)
                                                @RequestPart(value = "files",required = false) List<MultipartFile> files ,
                                                BindingResult bindingResult){
        Integer InsertResult = 0;

        try{
            InsertResult = noticeService.noticeCreate(dto,files);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),InsertResult);
    }

    @Operation(summary = "공지게시글 수정", description = "공지게시글 수정화면에서 게시글을 수정한다.",responses = {
            @ApiResponse(responseCode = "201",description = "정상적으로 게시글을 수정하는 경우")
    })
    @PatchMapping(value = "/update/{notice_id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>noticeUpdate(@Parameter(name = "notice_id",description = "공지게시글 번호",required = true)
                                               @PathVariable("notice_id")Integer noticeId,
                                               @Parameter(name = "updateDto",description = "공지게시글 수정 dto",required = false)
                                               @RequestPart(value = "updateDto",required = true) NoticeRequest dto,
                                               @Parameter(name = "files",description = "공지게시글 첨부파일",required = false)
                                               @RequestPart(value = "files",required = false) List<MultipartFile>files){
        Integer UpdateResult = 0;

        try{
            UpdateResult = noticeService.noticeUpdate(noticeId,dto,files);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }
    
    @Operation(summary = "공지게시글 삭제", description = "공지게시글 수정화면에서 게시글을 삭제한다.")
    @DeleteMapping("/delete/{notice_id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public CommonResponse<?>noticeDelete(@Parameter(name = "notice_id",description = "공지게시글 번호",required = true)
                                         @PathVariable("notice_id")Integer noticeId){

        try{
            noticeService.noticeDelete(noticeId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}

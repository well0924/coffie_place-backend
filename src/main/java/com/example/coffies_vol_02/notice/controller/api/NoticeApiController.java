package com.example.coffies_vol_02.notice.controller.api;

import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Parameter;
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

    @ApiOperation(value = "공지 게시판 목록", notes = "공지게시판 페이지에서 목록을 보여준다.")
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

    @ApiOperation(value = "공지 게시판 검색")
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

    @ApiOperation(value = "공지게시글 조회", notes = "공지게시글을 단일 조회한다.")
    @GetMapping("/detail/{notice_id}")
    public CommonResponse<NoticeResponse>noticeDetail(@Parameter(name = "notice_id",description = "공지게시글 번호",required = true)
                                                      @PathVariable("notice_id")Integer noticeId){
        NoticeResponse detail = noticeService.findNotice(noticeId);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @ApiOperation(value = "공지게시글 작성", notes = "공지게시글 작성화면에서 게시글을 작성한다.")
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

    @ApiOperation(value = "공지게시글 수정", notes = "공지게시글 수정화면에서 게시글을 수정한다.")
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
    
    @ApiOperation(value = "공지게시글 삭제", notes = "공지게시글 수정화면에서 게시글을 삭제한다.")
    @DeleteMapping("/delete/{notice_id}")
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

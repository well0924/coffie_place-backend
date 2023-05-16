package com.example.coffies_vol_02.Notice.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Api(tags = "Notice Api Controller",value = "공지게시판 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {
    private final NoticeService noticeService;

    @Operation(summary = "공지 게시판 목록",description = "공지게시판페이지에서 목록을 보여준다.")
    @GetMapping("/list")
    public CommonResponse<Page<NoticeBoardDto.BoardResponseDto>>noticeList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<NoticeBoardDto.BoardResponseDto> list = null;

        try{
            list = noticeService.noticeList(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "공지 게시판 검색")
    @GetMapping("/search")
    public CommonResponse<Page<NoticeBoardDto.BoardResponseDto>>noticeSearchList(@RequestParam String searchVal,@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<NoticeBoardDto.BoardResponseDto> list = null;

        try{
            list = noticeService.noticeSearchList(searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "공지게시글 조회",description = "공지게시글을 단일 조회한다.")
    @GetMapping("/detail/{notice_id}")
    public CommonResponse<NoticeBoardDto.BoardResponseDto>noticeDetail(@PathVariable("notice_id")Integer noticeId){
        NoticeBoardDto.BoardResponseDto detail = new NoticeBoardDto.BoardResponseDto();

        try{
            detail = noticeService.noticeDetail(noticeId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "공지게시글 작성",description = "공지게시글 작성화면에서 게시글을 작성한다.")
    @PostMapping(value = "/write")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>noticeWrite(@Valid @ModelAttribute NoticeBoardDto.BoardRequestDto dto, BindingResult bindingResult){
        Integer InsertResult = 0;

        try{
            InsertResult = noticeService.noticeWrite(dto,dto.getFiles());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),InsertResult);
    }

    @Operation(summary = "공지게시글 수정",description = "공지게시글 수정화면에서 게시글을 수정한다.")
    @PatchMapping("/update/{notice_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>noticeUpdate(@PathVariable("notice_id")Integer noticeId,@ModelAttribute NoticeBoardDto.BoardRequestDto dto){
        Integer UpdateResult = 0;

        try{
            UpdateResult = noticeService.noticeUpdate(noticeId,dto,dto.getFiles());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }
    
    @Operation(summary = "공지게시글 삭제",description = "공지게시글 수정화면에서 게시글을 삭제한다.")
    @DeleteMapping("/delete/{notice_id}")
    public CommonResponse<?>noticeDelete(@PathVariable("notice_id")Integer noticeId){
        try{
            noticeService.noticeDelete(noticeId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}

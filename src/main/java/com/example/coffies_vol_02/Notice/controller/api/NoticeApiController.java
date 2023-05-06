package com.example.coffies_vol_02.Notice.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Api(tags = "Notice Api Controller",value = "공지게시판 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {
    private final NoticeService noticeService;

    @ApiOperation(value = "공지 게시판 목록")
    @GetMapping("/list")
    public CommonResponse<Page<NoticeBoardDto.BoardResponseDto>>noticeList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
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
    public CommonResponse<Page<NoticeBoardDto.BoardResponseDto>>noticeSearchList(@RequestParam String searchVal,@PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<NoticeBoardDto.BoardResponseDto> list = null;

        try{
            list = noticeService.noticeSearchList(searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
    
    @ApiOperation(value = "공지게시글 단일 조회")
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

    @ApiOperation(value = "공지게시글 작성")
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

    @ApiOperation(value = "공지게시글 수정")
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
    
    @ApiOperation(value = "공지게시글 삭제")
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

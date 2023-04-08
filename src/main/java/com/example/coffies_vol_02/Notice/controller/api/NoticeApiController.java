package com.example.coffies_vol_02.Notice.controller.api;

import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.service.NoticeService;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Encoding;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Notice api",value = "공지게시판 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/notice")
public class NoticeApiController {
    private final NoticeService noticeService;

    @GetMapping("/list")
    public CommonResponse<?>noticeList(@PageableDefault Pageable pageable){
        Page<NoticeBoardDto.BoardResponseDto> list = noticeService.noticeList(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @GetMapping("/detail/{notice_id}")
    public CommonResponse<?>noticeDetail(@PathVariable("notice_id")Integer noticeId){
        NoticeBoardDto.BoardResponseDto detail = noticeService.noticeDetail(noticeId);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @PostMapping(value = "/write")
    public CommonResponse<?>noticeWrite(@Valid @RequestPart(value = "dto") NoticeBoardDto.BoardRequestDto dto, BindingResult bindingResult,@RequestPart(value = "files",required = false) List<MultipartFile> files) throws Exception {
        int InsertResult = noticeService.noticeWrite(dto,files);
        return new CommonResponse<>(HttpStatus.OK.value(),InsertResult);
    }

    @PatchMapping("/update/{notice_id}")
    public CommonResponse<?>noticeUpdate(@PathVariable("notice_id")Integer noticeId,@RequestPart("dto") NoticeBoardDto.BoardRequestDto dto,@RequestPart("files") List<MultipartFile>files) throws Exception {
        int UpdateResult = noticeService.noticeUpdate(noticeId,dto,files);
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @DeleteMapping("/delete/{notice_id}")
    public CommonResponse<?>noticeDelete(@PathVariable("notice_id")Integer noticeId) throws Exception {
        noticeService.noticeDelete(noticeId);
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }
}

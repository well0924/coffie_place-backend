package com.example.coffies_vol_02.Attach.controller;

import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "file api",value = "파일 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final AttachService attachService;

    @GetMapping("/filelist")
    public CommonResponse<?>fileList(@PathVariable("board_id")Integer boardId) throws Exception {
        List<AttachDto> list = attachService.boardfilelist(boardId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @GetMapping("/noticefilelist")
    public CommonResponse<?>noticefileList(@PathVariable("notice_id")Integer noticeId)throws Exception{
        List<AttachDto>list = attachService.noticefilelist(noticeId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }
}

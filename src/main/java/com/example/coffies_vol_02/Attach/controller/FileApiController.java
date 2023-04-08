package com.example.coffies_vol_02.Attach.controller;

import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Api(tags = "file api",value = "파일 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final AttachService attachService;

    @GetMapping("/filelist/{board_id}")
    public CommonResponse<?>fileList(@PathVariable("board_id")Integer boardId) throws Exception {
        List<AttachDto> list = attachService.boardfilelist(boardId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @GetMapping("/noticefilelist/{notice_id}")
    public CommonResponse<?>noticefileList(@PathVariable("notice_id")Integer noticeId)throws Exception{
        List<AttachDto>list = attachService.noticefilelist(noticeId);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @GetMapping("/download/{file_name}")
    public ResponseEntity<Resource>fileDownload(@PathVariable("file_name")String fileName) throws IOException {
        AttachDto getFile = attachService.getFile(fileName);
        Path path = Paths.get(getFile.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + getFile.getOriginFileName() + "\"")
                .body(resource);
    }
}

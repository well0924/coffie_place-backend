package com.example.coffies_vol_02.Attach.controller;

import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Api(tags = "file api controller",value = "파일 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final AttachService attachService;
    private final PlaceService placeService;

    @ApiOperation(value = "자유게시판 첨부파일 다운로드")
    @GetMapping("/download/{file_name}")
    public ResponseEntity<Resource>BoardFileDownload(@PathVariable("file_name")String fileName) throws IOException {
        AttachDto getFile = attachService.getFreeBoardFile(fileName);
        Path path = Paths.get(getFile.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(getFile.getOriginFileName(), "UTF-8") + "\"")
                .body(resource);
    }

    @ApiOperation(value = "공지게시판 첨부파일 다운로드")
    @GetMapping("/notice/download/{file_name}")
    public ResponseEntity<Resource>NoticeFileDownload(@PathVariable("file_name")String fileName) throws IOException {
        AttachDto getFile = attachService.getNoticeBoardFile(fileName);
        Path path = Paths.get(getFile.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + URLEncoder.encode(getFile.getOriginFileName(), "UTF-8") + "\"")
                .body(resource);
    }
    @ApiOperation("가게 목록 엑셀 다운로드")
    @GetMapping("/place-download")
    public ResponseEntity getPlaceListDownload(HttpServletResponse response, boolean excelDownload){
        return ResponseEntity.ok(placeService.getPlaceList(response,excelDownload));
    }
}

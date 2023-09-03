package com.example.coffies_vol_02.attach.controller;

import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.place.service.PlaceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
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

@Api(tags = "File api controller",value = "파일 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {
    private final PlaceService placeService;
    private final AttachService attachService;

    @ApiOperation(value = "자유게시판 첨부파일 다운로드", notes = "자유게시판에서 첨부파일을 다운로드한다.")
    @GetMapping("/{file-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Resource>BoardFileDownload(@Parameter(description = "첨부파일명",required = true) @PathVariable("file-name")String fileName) throws IOException {
        AttachDto getFile = attachService.getFreeBoardFile(fileName);
        Path path = Paths.get(getFile.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(getFile.getOriginFileName(), "UTF-8") + "\"")
                .body(resource);
    }

    @ApiOperation(value = "공지게시판 첨부파일 다운로드", notes = "공지게시판에서 첨부파일을 다운로드한다.")
    @GetMapping("/notice/{file-name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Resource>NoticeFileDownload(@Parameter(description = "첨부파일명",required = true) @PathVariable("file-name")String fileName) throws IOException {
        AttachDto getFile = attachService.getNoticeBoardFile(fileName);
        Path path = Paths.get(getFile.getFilePath());
        Resource resource = new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + URLEncoder.encode(getFile.getOriginFileName(), "UTF-8") + "\"")
                .body(resource);
    }
    @ApiOperation(value = "가게 목록 엑셀 다운로드", notes = "가게 목록을 엑셀파일로 다운로드한다.")
    @GetMapping("/place-download")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity getPlaceListDownload(HttpServletResponse response, boolean excelDownload){

        return ResponseEntity.ok(placeService.getPlaceList(response,excelDownload));
    }
}

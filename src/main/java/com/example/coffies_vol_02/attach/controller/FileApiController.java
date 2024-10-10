package com.example.coffies_vol_02.attach.controller;

import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.excel.ExcelService;
import com.example.coffies_vol_02.config.exception.Dto.DownloadResponseDto;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "File api controller",value = "파일 api 컨트롤러")
@RestController
@AllArgsConstructor
@RequestMapping("/api/file")
public class FileApiController {

    private final PlaceRepository placeRepository;

    private final AttachService attachService;

    @Operation(summary = "자유 게시판 첨부파일 목록", description = "자유 게시글에 첨부된 파일목록을 보여준다.", responses = {
            @ApiResponse(responseCode = "204")
    })
    @GetMapping("/board/{board_id}")
    public ResponseEntity<List<AttachDto>>BoardAttachFileList(@Parameter(description = "자유 게시글 번호" , required = true)
                                                              @PathVariable("board_id")Integer boardId) throws Exception {
        List<AttachDto>boardAttachList = attachService.boardfilelist(boardId);
        return ResponseEntity
                .ok(boardAttachList);
    }

    @Operation(summary = "공지 게시판 첨부파일 목록", description = "공지 게시글에서 첨부된 파일목록을 보여준다.", responses = {
            @ApiResponse(responseCode = "204")
    })
    @GetMapping("/notice/{notice_id}")
    public ResponseEntity<List<AttachDto>>noticeAttachFileList(@Parameter(description = "공지게시글 번호", required = true)
                                                               @PathVariable("notice_id")Integer noticeId) throws Exception {
        List<AttachDto>noticeAttachList = attachService.noticefilelist(noticeId);
        return ResponseEntity.ok(noticeAttachList);
    }

    @Operation(summary = "자유게시판 첨부파일 다운로드", description = "자유게시판에서 첨부파일을 다운로드한다.",responses = {
            @ApiResponse(responseCode = "204")
    })
    @GetMapping("/board/download/{file-name}")
    public ResponseEntity<Resource>BoardFileDownload(@Parameter(description = "첨부파일명",required = true)
                                                     @PathVariable("file-name")String fileName) throws IOException {

        AttachDto getFile = attachService.getFreeBoardFile(fileName);

        return getResourceResponseEntity(getFile);
    }

    @Operation(summary = "공지게시판 첨부파일 다운로드",description = "공지게시판에서 첨부파일을 다운로드한다.",responses = {
            @ApiResponse(responseCode = "204")
    })
    @GetMapping("/notice/download/{file-name}")
    public ResponseEntity<Resource>NoticeFileDownload(@Parameter(description = "첨부파일명",required = true) @PathVariable("file-name")String fileName) throws IOException {

        AttachDto getFile = attachService.getNoticeBoardFile(fileName);

        return getResourceResponseEntity(getFile);
    }

    @Operation(summary = "가게 목록 엑셀 다운로드",description = "가게 목록을 엑셀파일로 다운로드한다.",responses = {
            @ApiResponse(responseCode = "204")
    })
    @GetMapping("/place-download")
    public DownloadResponseDto<?> getPlaceListDownload(HttpServletRequest req, HttpServletResponse res) throws Exception {

        List<Place>list = placeRepository.findAll();

        List<PlaceResponseDto>result = list
                .stream()
                .map(place->new PlaceResponseDto(place))
                .collect(Collectors.toList());

        ExcelService<PlaceResponseDto>excelList = new ExcelService<>(result,PlaceResponseDto.class);

        excelList.downloadExcel(res);

        return new DownloadResponseDto<>();
    }

    private ResponseEntity<Resource> getResourceResponseEntity(AttachDto getFile) {
        if (getFile == null) {
            return ResponseEntity.notFound().build(); // 파일이 없을 경우 404 반환
        }

        Path path = Paths.get(getFile.getFilePath());

        if (!Files.exists(path)) {
            return ResponseEntity.notFound().build(); // 파일이 존재하지 않을 경우 404 반환
        }

        try {
            String mimeType = Files.probeContentType(path);
            if (mimeType == null) {
                mimeType = MediaType.APPLICATION_OCTET_STREAM_VALUE; // 기본 MIME 타입
            }

            Resource resource = new InputStreamResource(Files.newInputStream(path));
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mimeType)) // MIME 타입을 동적으로 설정
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + URLEncoder.encode(getFile.getOriginFileName(), "UTF-8") + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null); // 파일을 열 수 없을 경우 500 반환
        }
    }
}

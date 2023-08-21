package com.example.coffies_vol_02.board.controller.api;

import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.links.Link;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Api(tags = "Board api",description = "자유게시판 관련 api 컨트롤러")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    @Operation(summary = "게시글 목록", description = "자유게시판에서 목록을 조회하는 컨트롤러",responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping(path = "/list")
    public CommonResponse<?>boardList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){

        Page<BoardResponse> list = boardService.boardAllList(pageable);
        //게시글이 없는 경우
        if(list.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND);
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "게시글 검색",description = "자유게시판에서 게시물을 검색하는 컨트롤러",responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping(path = "/search")
    public CommonResponse<?>boardSearch(
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable,
            @Parameter(description = "게시물 검색 타입",in = ParameterIn.QUERY)
            @RequestParam(value = "searchType",required = false) SearchType searchType,
            @Parameter(description = "게시글에 사용되는 검색어",in=ParameterIn.QUERY)
            @RequestParam(value = "searchVal",required = false) String searchVal){

        Page<BoardResponse> list = null;
        //검색어가 없는 경우
        if(searchVal==null||searchVal.equals("")||searchType.getValue()==null||searchType.getValue().equals("")){
            return new CommonResponse<>(HttpStatus.OK.value(),ERRORCODE.NOT_SEARCH_VALUE.getMessage());
        }

        try {
            list = boardService.boardSearchAll(searchType,searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "게시글 단일 조회", description = "자유게시판에서 게시글을 단일 조회하는 컨트롤러",responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping(path = "/detail/{board-id}")
    public CommonResponse<?>findBoard(@Parameter(description = "게시글 단일조회에 필요한 게시글 번호",required = true,in = ParameterIn.PATH)
                                      @PathVariable("board-id") Integer boardId){
        BoardResponse detail = boardService.findBoard(boardId);
        if(detail==null){
            throw new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND);
        }
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "게시글 작성", description = "자유게시판 글작성화면에서 게시글 작성 및 파일첨부를 할 수 있다.")
    @PostMapping(path="/write", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardWrite(   @RequestBody(description = "자유게시판 요청 dto",required = true)
                                                @Valid @RequestPart(value = "boardDto") BoardRequest dto,
                                                @Parameter(name = "files",description = "자유게시판 첨부파일",required = false)
                                                @RequestPart(value = "files") List<MultipartFile> files,
                                                BindingResult bindingResult,
                                                @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer WriteResult = 0;

        try {
            WriteResult = boardService.boardCreate(dto,files,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @Operation(summary = "게시글 수정", description = "자유게시판 화면에서 게시글을 수정하는 컨트롤러")
    @PutMapping(path = "/update/{board-id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardUpdate(@Parameter(description = "자유게시글의 게시글 번호",required = true,in=ParameterIn.PATH)
                                              @PathVariable("board-id") Integer boardId,
                                              @RequestBody(description = "자유게시판 요청 dto",required = true)
                                              @RequestPart(value = "updateDto") BoardRequest dto,
                                              @Parameter(name = "files",description = "자유게시판 첨부파일",required = false)
                                              @RequestPart(value = "files")List<MultipartFile>files,
                                              @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer UpdateResult = 0;

        try{
            UpdateResult = boardService.BoardUpdate(boardId,dto,customUserDetails.getMember(),files);
        }catch (Exception  e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @Operation(summary = "게시글 삭제", description = "자유게시판에서 게시글을 삭제")
    @DeleteMapping(path = "/delete/{board-id}")
    public CommonResponse<?>boardDelete(@Parameter(description = "자유게시글의 게시글 번호",required = true,in=ParameterIn.PATH)
                                        @PathVariable("board-id")Integer boardId,
                                        @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        try {
            boardService.BoardDelete(boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }

    @Operation(summary = "자유게시판 비밀번호 입력",description = "자유게시글에서 비밀번호입력 화면에서 비밀번호가 있는 경우에는 비밀번호를 입력해서 게시글을 조회하는 컨트롤러")
    @GetMapping(path = "/password/{board-id}/{password}")
    public CommonResponse<BoardResponse>passwordCheck(@Parameter(description = "게시글 번호",required = true,in=ParameterIn.PATH)
                                                      @PathVariable("board-id")Integer boardId,
                                                      @Parameter(description = "게시글 비밀번호",required = true,in = ParameterIn.PATH)
                                                      @PathVariable("password") String password,
                                                      @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        BoardResponse result = null;

        try{
            result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

    @Operation(summary = "최근에 작성한 글",description = "자유게시판 글 중에서 최신순으로 글 5개를 출력")
    @GetMapping("/recentboard")
    public CommonResponse<List<BoardResponse>>recentBoardList(){

        List<BoardResponse>result = new ArrayList<>();

        try{
            result = boardService.recentBoardList();
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse(HttpStatus.OK.value(),result);
    }
}

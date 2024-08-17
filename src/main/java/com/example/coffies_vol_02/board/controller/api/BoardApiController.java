package com.example.coffies_vol_02.board.controller.api;

import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;

@Log4j2
@Api(tags = "Board api",value = "자유게시판 관련 api 컨트롤러")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {

    private final BoardService boardService;

    @Operation(summary = "자유 게시글 목록", description = "자유게시판에서 목록을 조회하는 컨트롤러", responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping(path = "/")
    public CommonResponse<Page<BoardResponse>>listFreeBoard(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){

        Page<BoardResponse> list = boardService.listFreeBoard(pageable);
        //게시글이 없는 경우
        if(list.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND);
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "자유 게시글 검색", description = "자유게시판에서 게시물을 검색하는 컨트롤러", responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @GetMapping(path = "/search")
    public CommonResponse<?>searchFreeBoardList(
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable,
            @Parameter(description = "게시물 검색 타입",in = ParameterIn.QUERY)
            @RequestParam(value = "searchType",required = false) String searchType,
            @Parameter(description = "게시글에 사용되는 검색어",in=ParameterIn.QUERY)
            @RequestParam(value = "searchVal",required = false) String searchVal){

        //검색어가 없는 경우
        if (StringUtils.isBlank(searchType) || StringUtils.isBlank(searchVal)) {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST, ERRORCODE.NOT_SEARCH_VALUE);
        }

        Page<BoardResponse> list = boardService.searchFreeBoard(SearchType.valueOf(searchType),searchVal,pageable);
        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "자유 게시글 단일 조회", description = "자유게시판에서 게시글을 단일 조회하는 컨트롤러",responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @GetMapping(path = "/{board-id}")
    public CommonResponse<?>findFreeBoardById(@Parameter(description = "게시글 단일조회에 필요한 게시글 번호",required = true,in = ParameterIn.PATH)
                                      @PathVariable("board-id") Integer boardId){

        BoardResponse detail = boardService.findFreeBoard(boardId);

        if(detail == null){
            throw new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND);
        }

        return new CommonResponse<>(HttpStatus.OK,detail);
    }

    @Operation(summary = "자유 게시글 이전글/다음글 조회", description = "자유게시판에서 게시글을 단일 조회시 이전글/다음글을 보여주는 컨트롤러",responses = {
            @ApiResponse(responseCode = "200",description = "정상적으로 응답하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class)))
    })
    @Secured({"ROLE_ADMIN","ROLE_USER"})
    @GetMapping("/previous-next/{id}")
    public CommonResponse<?>findByPreviousNextBoard(@Parameter(description = "게시글 번호", required = true) @PathVariable("id")Integer boardId) {
        List<BoardNextPreviousInterface> list = boardService.boardNextPrevious(boardId);
        return new CommonResponse<>(HttpStatus.OK,list);
    }

    @Operation(summary = "게시글 작성", description = "자유게시판 글작성화면에서 게시글 작성 및 파일첨부를 할 수 있다.",responses = {
            @ApiResponse(responseCode = "201",description = "게시글이 정상적으로 작성이 되는 경우",content = @Content(mediaType = "MULTIPART_FORM_DATA_VALUE"))
    })
    @PostMapping(path="/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE,MediaType.APPLICATION_JSON_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>createFreeBoard(@RequestBody(description = "자유게시판 요청 dto",required = true)
                                                @Valid @RequestPart(value = "boardDto") BoardRequest dto,
                                                @Parameter(name = "files",description = "자유게시판 첨부파일")
                                                @RequestPart(value = "files") List<MultipartFile> files,
                                                BindingResult bindingResult,
                                                @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails)throws Exception{

        Integer writeResult = boardService.createFreeBoard(dto,files,customUserDetails.getMember());

        if (writeResult > 0) {
            return new CommonResponse<>(HttpStatus.CREATED, writeResult);
        } else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.BOARD_FAIL);
        }
    }

    @Operation(summary = "게시글 수정", description = "자유게시판 화면에서 게시글을 수정하는 컨트롤러",responses = {
            @ApiResponse(responseCode = "201",description = "게시글을 정상적으로 수정을 하는 경우",content = @Content(mediaType = "MULTIPART_FORM_DATA_VALUE"))
    })
    @PutMapping(path = "/{board-id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<Integer>updateFreeBoard(@Parameter(description = "자유게시글의 게시글 번호",required = true,in=ParameterIn.PATH)
                                              @PathVariable("board-id") Integer boardId,
                                              @RequestBody(description = "자유게시판 요청 dto",required = true)
                                              @RequestPart(value = "updateDto") BoardRequest dto,
                                              @Parameter(name = "files",description = "자유게시판 첨부파일")
                                              @RequestPart(value = "files")List<MultipartFile>files,
                                              @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails)throws Exception{

        Integer updateResult = boardService.updateFreeBoard(boardId,dto,customUserDetails.getMember(),files);

        if (updateResult > 0) {
            return new CommonResponse<>(HttpStatus.OK, updateResult);
        } else {
            return new CommonResponse<>(HttpStatus.BAD_REQUEST,ERRORCODE.BOARD_FAIL);
        }
    }

    @Operation(summary = "게시글 삭제", description = "자유게시판에서 게시글을 삭제",responses = {
            @ApiResponse(responseCode = "204",description = "게시글을 정상적으로 삭제하는 경우")
    })
    @DeleteMapping(path = "/{board-id}")
    public CommonResponse<?>deleteFreeBoard(@Parameter(description = "자유게시글의 게시글 번호",required = true,in=ParameterIn.PATH)
                                        @PathVariable("board-id")Integer boardId,
                                        @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails)throws Exception{

        boardService.deleteFreeBoard(boardId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }

    @Operation(summary = "자유게시판 비밀번호 입력",description = "자유게시글에서 비밀번호입력 화면에서 비밀번호가 있는 경우에는 비밀번호를 입력해서 게시글을 조회하는 컨트롤러",responses = {
            @ApiResponse(responseCode = "201",description = "비밀번호를 정상적으로 입력을 하는 경우",content = @Content(mediaType = "application/json",schema = @Schema(implementation = BoardResponse.class))),
            @ApiResponse(responseCode = "400",description = "비밀번호를 올바르게 입력하지 않은 경우")
    })
    @GetMapping(path = "/{board-id}/{password}")
    public CommonResponse<BoardResponse>passwordCheckFreeBoard(@Parameter(description = "게시글 번호",required = true,in=ParameterIn.PATH)
                                                      @PathVariable("board-id")Integer boardId,
                                                      @Parameter(description = "게시글 비밀번호",required = true,in = ParameterIn.PATH)
                                                      @PathVariable("password") String password,
                                                      @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        BoardResponse result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK,result);
    }

    @Operation(summary = "최근에 작성한 글",description = "자유게시판 글 중에서 최신순으로 글 5개를 출력",responses = {
            @ApiResponse(responseCode = "201",description = "게시글을 정상적으로 출력하는 경우")
    })
    @GetMapping("/recent-board")
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<List<BoardResponse>>recentFreeBoardList(){

        List<BoardResponse>result = boardService.findFreeBoardTop5();

        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }
}

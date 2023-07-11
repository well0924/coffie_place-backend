package com.example.coffies_vol_02.board.controller.api;

import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
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
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;

@Log4j2
@Api(tags = "Board api",value = "자유게시판 관련 api 컨트롤러")
@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    @ApiOperation(value = "게시글 목록",notes = "자유게시판에서 목록을 조회하는 컨트롤러")
    @GetMapping(path = "/list")
    public CommonResponse<Page<BoardResponse>>boardList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<BoardResponse> list = null;

        try {
            list = boardService.boardAllList(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "게시글 검색",description = "자유게시판에서 게시물을 검색하는 컨트롤러")
    @GetMapping(path = "/search")
    public CommonResponse<?>boardSearch(
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable,
            @RequestParam(value = "searchVal",required = false) String searchVal){
        Page<BoardResponse> list = null;

        if(searchVal==null||searchVal==""){
            return new CommonResponse<>(HttpStatus.OK.value(),ERRORCODE.NOT_SEARCH_VALUE.getMessage());
        }

        try {
            list = boardService.boardSearchAll(searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "게시글 단일 조회",description = "자유게시판에서 게시글을 단일 조회하는 컨트롤러")
    @GetMapping(path = "/detail/{id}")
    public CommonResponse<?>findBoard(@PathVariable("id") Integer boardId){
        BoardResponse detail = null;

        try {
            detail = boardService.findBoard(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "게시글 작성",description = "자유게시판 글작성화면에서 게시글 작성 및 파일첨부를 할 수 있다.")
    @PostMapping(path="/write",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>boardWrite(@Valid @ModelAttribute BoardRequest dto, BindingResult bindingResult, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer WriteResult = 0;

        try {
            boardService.boardCreate(dto,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @Operation(summary = "게시글 수정",description = "자유게시판 화면에서 게시글을 수정하는 컨트롤러")
    @PutMapping(path = "/update/{board_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardUpdate(@PathVariable("board_id") Integer boardId,@ModelAttribute BoardRequest dto,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer UpdateResult = 0;

        try{
            boardService.BoardUpdate(boardId,dto,customUserDetails.getMember(),dto.files());
        }catch (Exception  e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @Operation(summary = "게시글 삭제",description = "자유게시판에서 게시글을 삭제")
    @DeleteMapping(path = "/delete/{board_id}")
    public CommonResponse<?>boardDelete(@PathVariable("board_id")Integer boardId, @AuthenticationPrincipal CustomUserDetails customUserDetails){

        try {
            boardService.BoardDelete(boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }

    @Operation(summary = "자유게시판 비밀번호 입력",description = "자유게시글에서 비밀번호입력 화면에서 비밀번호가 있는 경우에는 비밀번호를 입력해서 게시글을 조회하는 컨트롤러")
    @GetMapping(path = "/password/{board_id}/{password}")
    public CommonResponse<BoardResponse>passwordCheck(@PathVariable("board_id")Integer boardId,@PathVariable("password") String password,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        BoardResponse result = null;

        try{
            result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

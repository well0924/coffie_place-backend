package com.example.coffies_vol_02.board.controller.api;

import com.example.coffies_vol_02.board.domain.dto.request.BoardRequestDto;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
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

    @ApiOperation(value = "게시글 목록",notes = "자유게시판 목록")
    @GetMapping(path = "/list")
    public CommonResponse<Page<BoardResponseDto>>boardList(@ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<BoardResponseDto> list = null;

        try {
            list = boardService.boardAllList(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "게시글 검색",description = "자유게시판 목록 검색")
    @GetMapping(path = "/search")
    public CommonResponse<Page<BoardResponseDto>>boardSearch(
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable,
            @RequestParam(value = "searchVal",required = false) String searchVal){
        Page<BoardResponseDto> list = null;

        try {
            list = boardService.boardSearchAll(searchVal,pageable);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @Operation(summary = "게시글 단일 조회",description = "자유게시판 단일 조회")
    @GetMapping(path = "/detail/{id}")
    public CommonResponse<?>findBoard(@PathVariable("id") Integer boardId){
        BoardResponseDto detail = new BoardResponseDto();

        try {
            detail = boardService.findBoard(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @Operation(summary = "게시글 작성",description = "자유게시판에서 게시글 작성 및 파일첨부를 할 수 있다.")
    @PostMapping(path="/write",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>boardWrite(@Valid @ModelAttribute BoardRequestDto dto, BindingResult bindingResult, @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer WriteResult = 0;

        try {
            boardService.boardCreate(dto,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @Operation(summary = "게시글 수정",description = "자유게시판에서 게시글을 수정")
    @PutMapping(path = "/update/{board_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardUpdate(@PathVariable("board_id") Integer boardId,@ModelAttribute BoardRequestDto dto,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer UpdateResult = 0;

        try{
            boardService.BoardUpdate(boardId,dto,customUserDetails.getMember(),dto.getFiles());
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

    @Operation(summary = "자유게시판 비밀번호 입력",description = "게시글에 비밀번호가 있는 경우에는 비밀번호를 입력해")
    @GetMapping(path = "/password/{board_id}/{password}")
    public CommonResponse<BoardResponseDto>passwordCheck(@PathVariable("board_id")Integer boardId,@PathVariable("password") String password,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        BoardResponseDto result = new BoardResponseDto();

        try{
            result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

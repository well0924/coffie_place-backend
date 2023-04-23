package com.example.coffies_vol_02.Board.controller.api;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @GetMapping("/boardlist")
    public CommonResponse<Page<BoardDto.BoardResponseDto>>boardList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<BoardDto.BoardResponseDto> list = boardService.boardAll(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "게시글 검색",notes = "자유게시판 목록 검색")
    @GetMapping("/search")
    public CommonResponse<Page<BoardDto.BoardResponseDto>>boardSearch(@PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable,@RequestParam(value = "searchVal",required = false) String searchVal){
        Page<BoardDto.BoardResponseDto> list = boardService.boardSearchAll(searchVal,pageable);

        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @ApiOperation(value = "게시글 단일 조회",notes = "자유게시판 단일 조회")
    @GetMapping("/{id}")
    public CommonResponse<BoardDto.BoardResponseDto>boardDetail(@PathVariable("id") Integer boardId){
        BoardDto.BoardResponseDto detail = boardService.boardDetail(boardId);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @ApiOperation(value = "게시글 작성",notes = "자유게시판에서 게시글 작성 및 파일첨부를 할 수 있다.")
    @PostMapping(path="/write",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<?>boardWrite(@Valid @ModelAttribute BoardDto.BoardRequestDto dto, BindingResult bindingResult,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {
        int WriteResult = boardService.boardSave(dto,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @ApiOperation(value = "게시글 수정",notes = "자유게시판에서 게시글을 수정")
    @PutMapping(path = "/update/{board_id}",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardUpdate(@PathVariable("board_id") Integer boardId,@ModelAttribute BoardDto.BoardRequestDto dto,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {
        int UpdateResult = boardService.BoardUpdate(boardId,dto,customUserDetails.getMember(),dto.getFiles());

        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }
    @ApiOperation(value = "게시글 삭제",notes = "자유게시판에서 게시글을 삭제")
    @DeleteMapping("/delete/{board_id}")
    public CommonResponse<?>boardDelete(@PathVariable("board_id")Integer boardId,@AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {

        boardService.BoardDelete(boardId,customUserDetails.getMember());

        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }

    @ApiOperation(value = "자유게시판 비밀번호 입력")
    @GetMapping("/password/{board_id}/{password}")
    public CommonResponse<BoardDto.BoardResponseDto>passwordChange(@PathVariable("board_id")Integer boardId,@PathVariable("password") String password,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        BoardDto.BoardResponseDto result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

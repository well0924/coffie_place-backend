package com.example.coffies_vol_02.Board.controller.api;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
public class BoardApiController {
    private final BoardService boardService;

    @GetMapping("/boardlist")
    public CommonResponse<?>boardList(@PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable){
        Page<BoardDto.BoardResponseDto> list = boardService.boardAll(pageable);
        return new CommonResponse<>(HttpStatus.OK.value(),list);
    }

    @GetMapping("/{id}")
    public CommonResponse<?>boardDetail(@PathVariable("id") Integer boardId){
        BoardDto.BoardResponseDto detail = boardService.boardDetail(boardId);
        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @PostMapping("/write")
    public CommonResponse<?>boardWrite(@Valid @RequestBody BoardDto.BoardRequestDto dto, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        int WriteResult = boardService.boardSave(dto,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @PatchMapping("/update/{board_id}")
    public CommonResponse<?>boardUpdate(@PathVariable("board_id") Integer boardId,@Valid @RequestBody BoardDto.BoardRequestDto dto, BindingResult bindingResult,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        int UpdateResult = boardService.BoardUpdate(boardId,dto,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @DeleteMapping("/delete/{board_id}")
    public CommonResponse<?>boardDelete(@PathVariable("board_id")Integer boardId,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        boardService.BoardDelete(boardId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }


}

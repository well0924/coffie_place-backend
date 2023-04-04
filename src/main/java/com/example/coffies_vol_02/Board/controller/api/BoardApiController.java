package com.example.coffies_vol_02.Board.controller.api;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.Config.Exception.Dto.CommonResponse;
import com.example.coffies_vol_02.Config.security.auth.CustomUserDetails;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@Api(tags = "Board api",value = "자유게시판 관련 api 컨트롤러")
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
    public CommonResponse<?>boardWrite(@Valid @RequestPart(value = "board") BoardDto.BoardRequestDto dto,
                                       BindingResult bindingResult,
                                       @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                       @RequestPart(value = "files",required = false) List<MultipartFile>files) throws Exception {

        int WriteResult = boardService.boardSave(dto,customUserDetails.getMember(),files);

        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @PatchMapping("/update/{board_id}")
    public CommonResponse<?>boardUpdate(@PathVariable("board_id") Integer boardId,
                                        @Valid @RequestPart(value = "board") BoardDto.BoardRequestDto dto,
                                        BindingResult bindingResult,
                                        @AuthenticationPrincipal CustomUserDetails customUserDetails,
                                        @RequestPart(value = "files",required = false) List<MultipartFile>files) throws Exception {
        int UpdateResult = boardService.BoardUpdate(boardId,dto,customUserDetails.getMember(),files);
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @DeleteMapping("/delete/{board_id}")
    public CommonResponse<?>boardDelete(@PathVariable("board_id")Integer boardId,@AuthenticationPrincipal CustomUserDetails customUserDetails) throws Exception {
        boardService.BoardDelete(boardId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }

    @GetMapping("/password/{board_id}/{password}")
    public CommonResponse<?>passwordChange(@PathVariable("board_id")Integer boardId,@PathVariable("password") String password,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        BoardDto.BoardResponseDto result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

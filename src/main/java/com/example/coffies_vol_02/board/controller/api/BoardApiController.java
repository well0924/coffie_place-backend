package com.example.coffies_vol_02.board.controller.api;

import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.exception.Dto.CommonResponse;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import io.swagger.annotations.*;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import java.util.List;

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

    @ApiOperation(value = "게시글 검색",notes = "자유게시판에서 게시물을 검색하는 컨트롤러")
    @GetMapping(path = "/search")
    public CommonResponse<?>boardSearch(
            @ApiIgnore @PageableDefault(sort = "id",direction = Sort.Direction.DESC, size = 5) Pageable pageable,
            @Parameter(description = "게시글에 사용되는 검색어",in=ParameterIn.QUERY) @RequestParam(value = "searchVal",required = false) String searchVal){
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

    @ApiOperation(value = "게시글 단일 조회", notes = "자유게시판에서 게시글을 단일 조회하는 컨트롤러")
    @GetMapping(path = "/detail/{id}")
    public CommonResponse<?>findBoard(@Parameter(description = "게시글 단일조회에 필요한 게시글 번호",required = true,in = ParameterIn.PATH) @PathVariable("id") Integer boardId){
        BoardResponse detail = null;

        try {
            detail = boardService.findBoard(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),detail);
    }

    @ApiOperation(value = "게시글 작성", notes = "자유게시판 글작성화면에서 게시글 작성 및 파일첨부를 할 수 있다.")
    @PostMapping(path="/write", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardWrite(@Valid @ModelAttribute BoardRequest dto,
                                             BindingResult bindingResult,
                                             @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer WriteResult = 0;

        try {
            boardService.boardCreate(dto,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }

        return new CommonResponse<>(HttpStatus.OK.value(),WriteResult);
    }

    @ApiOperation(value = "게시글 수정", notes = "자유게시판 화면에서 게시글을 수정하는 컨트롤러")
    @PutMapping(path = "/update/{board_id}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommonResponse<Integer>boardUpdate(@Parameter(description = "자유게시글의 게시글 번호",required = true,in=ParameterIn.PATH) @PathVariable("board_id") Integer boardId,@ModelAttribute BoardRequest dto,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        Integer UpdateResult = 0;

        try{
            boardService.BoardUpdate(boardId,dto,customUserDetails.getMember(),dto.files());
        }catch (Exception  e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),UpdateResult);
    }

    @ApiOperation(value = "게시글 삭제", notes = "자유게시판에서 게시글을 삭제")
    @DeleteMapping(path = "/delete/{board_id}")
    public CommonResponse<?>boardDelete(@Parameter(description = "자유게시글의 게시글 번호",required = true,in=ParameterIn.PATH) @PathVariable("board_id")Integer boardId,@ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){

        try {
            boardService.BoardDelete(boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),"Delete O.k");
    }

    @ApiOperation(value = "자유게시판 비밀번호 입력",notes = "자유게시글에서 비밀번호입력 화면에서 비밀번호가 있는 경우에는 비밀번호를 입력해서 게시글을 조회하는 컨트롤러")
    @GetMapping(path = "/password/{board_id}/{password}")
    public CommonResponse<BoardResponse>passwordCheck(@Parameter(description = "게시글 번호",required = true,in=ParameterIn.PATH) @PathVariable("board_id")Integer boardId,@Parameter(description = "게시글 비밀번호",required = true)@PathVariable("password") String password,@AuthenticationPrincipal CustomUserDetails customUserDetails){
        BoardResponse result = null;

        try{
            result = boardService.passwordCheck(password,boardId,customUserDetails.getMember());
        }catch (Exception e){
            e.printStackTrace();
        }
        return new CommonResponse<>(HttpStatus.OK.value(),result);
    }

}

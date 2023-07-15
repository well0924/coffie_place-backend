package com.example.coffies_vol_02.board.controller.view;

import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/page/board")
public class BoardViewController {
    private final BoardService boardService;
    private final BoardRepository boardRepository;
    private final AttachService attachService;

    @GetMapping("/list")
    public ModelAndView boardList(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                  @RequestParam(value = "searchVal",required = false)String searchVal)throws Exception{

        ModelAndView mv = new ModelAndView();

        Page<BoardResponse> boardList = null;

        try {
            boardList = boardService.boardAllList(pageable);

            if(searchVal != null){
                boardList = boardService.boardSearchAll(searchVal,pageable);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("boardList",boardList);
        mv.addObject("searchVal",searchVal);

        mv.setViewName("board/boardList");

        return mv;
    }

    @GetMapping("/detail/{board-id}")
    public ModelAndView boardDetail(@PathVariable("board-id") Integer boardId){

        ModelAndView mv = new ModelAndView();

        BoardResponse detail = null;
        List<AttachDto> attachList = new ArrayList<>();
        Optional<BoardNextPreviousInterface> previousBoard = Optional.empty();
        Optional<BoardNextInterface> nextBoard = Optional.empty();

        try{
            detail = boardService.findBoard(boardId);
            attachList = attachService.boardfilelist(boardId);
            previousBoard = boardService.findPreviousBoard(boardId);
            nextBoard = boardService.findNextBoard(boardId);
            //조회수 증가.
            boardRepository.ReadCountUpToDB(boardId,detail.readCount());

            mv.addObject("detail",detail);
            mv.addObject("file",attachList);

            if(nextBoard.isPresent()){
                mv.addObject("next",nextBoard);
            } else if(nextBoard.isEmpty()){
                mv.addObject("next",nextBoard);
            }
            if(previousBoard.isPresent()){
                mv.addObject("previous",previousBoard);
            }else if(previousBoard.isEmpty()){
                mv.addObject("previous",previousBoard);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.setViewName("board/detailBoard");

        return mv;
    }

    @GetMapping("/writePage")
    public ModelAndView writePage(){
        ModelAndView mv = new ModelAndView();

        String uuid = UUID.randomUUID().toString();
        String key = "free_"+uuid.substring(0,uuid.indexOf("-"));

        mv.addObject("fileGroupId", key);

        mv.setViewName("/board/writeBoard");

        return mv;
    }

    @GetMapping("/passwordCheck/{board_id}")
    public ModelAndView passwordCheck(@PathVariable("board_id") Integer boardId){
        ModelAndView mv = new ModelAndView();

        BoardResponse detail = null;

        try{
            detail = boardService.findBoard(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("pwd",detail);
        mv.setViewName("/board/passWordCheck");

        return mv;
    }

    @GetMapping("/modify/{board_id}")
    public ModelAndView modifyPage(@PathVariable("board_id") Integer boardId) {
        ModelAndView mv = new ModelAndView();

        BoardResponse detail = null;
        List<AttachDto> attachList= new ArrayList<>();

        try{
            detail = boardService.findBoard(boardId);
            attachList=attachService.boardfilelist(boardId);
        }catch (Exception e){
            e.printStackTrace();
        }
        mv.addObject("detail",detail);
        mv.addObject("file",attachList);

        mv.setViewName("/board/boardModify");

        return mv;
    }
}

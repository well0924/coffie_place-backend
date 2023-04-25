package com.example.coffies_vol_02.Board.controller.view;

import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.Config.Redis.RedisService;
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

import java.util.List;
import java.util.UUID;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/page/board")
public class BoardViewController {
    private final BoardService boardService;
    private final AttachService attachService;
    private final RedisService redisService;
    @GetMapping("/list")
    public ModelAndView boardList(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                  @RequestParam(value = "searchVal",required = false)String searchVal){
        ModelAndView mv = new ModelAndView();
        Page<BoardDto.BoardResponseDto> boardList = boardService.boardSearchAll(searchVal,pageable);

        mv.addObject("boardlist",boardList);
        mv.setViewName("/board/boardlist");

        return mv;
    }

    @GetMapping("/detail/{board_id}")
    public ModelAndView boardDetail(@PathVariable("board_id") Integer boardId)throws Exception{
        ModelAndView mv = new ModelAndView();

        BoardDto.BoardResponseDto detail = boardService.boardDetail(boardId);
        List<AttachDto> attachList = attachService.boardfilelist(boardId);
        //조회수 증가(캐시 적용)
        redisService.boardViewCount(boardId);

        mv.addObject("detail",detail);
        mv.addObject("file",attachList);

        mv.setViewName("/board/detailboard");

        return mv;
    }

    @GetMapping("/writePage")
    public ModelAndView writePage(){
        ModelAndView mv = new ModelAndView();

        String uuid = UUID.randomUUID().toString();
        String key = "free_"+uuid.substring(0,uuid.indexOf("-"));

        mv.addObject("fileGroupId", key);

        mv.setViewName("/board/writeboard");

        return mv;
    }

    @GetMapping("/passwordCheck/{board_id}")
    public ModelAndView passwordCheck(@PathVariable("board_id") Integer boardId){
        ModelAndView mv = new ModelAndView();

        BoardDto.BoardResponseDto detail = boardService.boardDetail(boardId);

        mv.addObject("pwd",detail);
        mv.setViewName("/board/passwordcheck");

        return mv;
    }

    @GetMapping("/modify/{board_id}")
    public ModelAndView modifyPage(@PathVariable("board_id") Integer boardId) throws Exception {
        ModelAndView mv = new ModelAndView();

        BoardDto.BoardResponseDto detail = boardService.boardDetail(boardId);
        List<AttachDto> attachList=attachService.boardfilelist(boardId);

        mv.addObject("detail",detail);
        mv.addObject("file",attachList);
        mv.setViewName("/board/boardmodify");

        return mv;
    }
}

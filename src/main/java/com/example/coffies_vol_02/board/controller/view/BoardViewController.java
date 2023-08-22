package com.example.coffies_vol_02.board.controller.view;

import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.constant.SearchType;
import com.example.coffies_vol_02.config.redis.RedisService;
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
    public ModelAndView boardList(@PageableDefault(page=0,size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                  @RequestParam(value = "searchType",required = false) SearchType searchType,
                                  @RequestParam(value = "searchVal",required = false)String searchVal)throws Exception{

        ModelAndView mv = new ModelAndView();

        Page<BoardResponse> boardList = null;

        try {
            boardList = boardService.boardAllList(pageable);
            //검색을 하는 경우
            if(searchVal!=null){
                boardList = boardService.boardSearchAll(searchType,searchVal,pageable);
            }
            log.info(boardList.hasPrevious());
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("boardList",boardList);
        mv.addObject("searchVal",searchVal);
        mv.addObject("searchType",searchType);

        mv.setViewName("board/boardList");

        return mv;
    }

    @GetMapping("/detail/{board-id}")
    public ModelAndView boardDetail(@PathVariable("board-id") Integer boardId)throws Exception{

        ModelAndView mv = new ModelAndView();

        BoardResponse detail;
        List<AttachDto> attachList;

        //게시글 조회수 캐시 적용
        redisService.boardViewCount(boardId);
        //게시글 조회
        detail = boardService.findBoard(boardId);
        //첨부파일
        attachList = attachService.boardfilelist(boardId);

        mv.addObject("detail",detail);
        mv.addObject("file",attachList);

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

    @GetMapping("/passwordCheck/{board-id}")
    public ModelAndView passwordCheck(@PathVariable("board-id") Integer boardId){
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

    @GetMapping("/modify/{board-id}")
    public ModelAndView modifyPage(@PathVariable("board-id") Integer boardId) {
        ModelAndView mv = new ModelAndView();

        BoardResponse detail = null;
        List<AttachDto> attachList= new ArrayList<>();

        try{
            //게시글 상세조회
            detail = boardService.findBoard(boardId);
            //첨부파일
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

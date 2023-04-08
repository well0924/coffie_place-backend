package com.example.coffies_vol_02.Main;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.service.NoticeService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@AllArgsConstructor
@RequestMapping("/page/main")
public class MainController {
    private final BoardService boardService;
    private final NoticeService noticeService;

    @GetMapping("/mainpage")
    public ModelAndView mainPage(Pageable pageable){
        ModelAndView  mv = new ModelAndView();

        Page<BoardDto.BoardResponseDto> boardList= boardService.boardAll(pageable);
        Page<NoticeBoardDto.BoardResponseDto>noticeList = noticeService.noticeList(pageable);

        mv.addObject("boardList",boardList);
        mv.addObject("noticelist",noticeList);

        mv.setViewName("/main/mian");

        return mv;
    }
}

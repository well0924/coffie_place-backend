package com.example.coffies_vol_02.Main;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.service.BoardService;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.service.NoticeService;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@AllArgsConstructor
@RequestMapping("/page/main")
public class MainController {
    private final BoardService boardService;
    private final NoticeService noticeService;

    @GetMapping("/main")
    public ModelAndView mainPage(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        ModelAndView  mv = new ModelAndView();

        Page<BoardDto.BoardResponseDto> boardList = boardService.boardAll(pageable);
        Page<NoticeBoardDto.BoardResponseDto>noticelist = noticeService.noticeList(pageable);

        mv.addObject("boardlist",boardList);
        mv.addObject("noticelist",noticelist);

        mv.setViewName("/mainPage/mainpage");

        return mv;
    }
}

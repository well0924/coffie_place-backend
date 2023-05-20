package com.example.coffies_vol_02.main;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponseDto;
import com.example.coffies_vol_02.notice.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/page/main")
public class MainController {
    private final BoardService boardService;
    private final NoticeService noticeService;

    @GetMapping("/main")
    public ModelAndView mainPage(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable){
        ModelAndView  mv = new ModelAndView();
        //게시글 목록
        Page<BoardResponseDto> boardList = boardService.boardAllList(pageable);
        //공지게시글 목록
        Page<NoticeResponseDto>noticeList = noticeService.noticeAllList(pageable);
        //top5 목록


        mv.addObject("boardlist",boardList);
        mv.addObject("noticelist",noticeList);

        mv.setViewName("/mainPage/mainpage");

        return mv;
    }
}

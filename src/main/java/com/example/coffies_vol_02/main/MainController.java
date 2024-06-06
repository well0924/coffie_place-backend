package com.example.coffies_vol_02.main;

import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.service.NoticeService;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.service.PlaceService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import springfox.documentation.annotations.ApiIgnore;

import java.util.List;

@Log4j2
@Controller
@AllArgsConstructor
@RequestMapping("/page/main")
public class MainController {

    private final BoardService boardService;
    private final NoticeService noticeService;
    private final PlaceService placeService;

    @GetMapping("/main")
    public ModelAndView mainPage(@PageableDefault(size = 5,sort = "id",direction = Sort.Direction.DESC) Pageable pageable,
                                 @ApiIgnore @AuthenticationPrincipal CustomUserDetails customUserDetails){
        ModelAndView  mv = new ModelAndView();

        Page<BoardResponse> boardList = null;
        Page<NoticeResponse>noticeList = null;
        Page<PlaceResponseDto>top5 = null;


        try {
            //평점이 높은 가게 top5
            top5 =placeService.cafePlaceByReviewRateTop5(pageable);

            //공지게시글 목록
            noticeList = noticeService.listNoticeBoard(pageable);
            //자유게시글 목록
            boardList = boardService.listFreeBoard(pageable);
        }catch (Exception e){
            e.printStackTrace();
        }

        mv.addObject("boardlist",boardList);
        mv.addObject("noticelist",noticeList);
        mv.addObject("top5",top5);

        mv.setViewName("/mainPage/mainpage");

        return mv;
    }
}

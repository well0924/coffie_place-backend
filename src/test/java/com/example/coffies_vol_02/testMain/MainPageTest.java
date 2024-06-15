package com.example.coffies_vol_02.testMain;

import com.example.coffies_vol_02.factory.BoardFactory;
import com.example.coffies_vol_02.factory.MemberFactory;
import com.example.coffies_vol_02.factory.NoticeFactory;
import com.example.coffies_vol_02.factory.PlaceFactory;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.board.service.BoardService;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
import com.example.coffies_vol_02.notice.service.NoticeService;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class MainPageTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private MockMvc mvc;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private NoticeBoardRepository noticeBoardRepository;

    @Mock
    private PlaceRepository placeRepository;

    @MockBean
    private BoardService boardService;

    @MockBean
    private NoticeService noticeService;

    private Member member;

    private Board board;

    private NoticeBoard noticeBoard;

    private Place place;

    private PlaceImage placeImage;

    BoardResponse boardResponseDto;

    NoticeResponse responseDto;

    private CustomUserDetails customUserDetails;

    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(SecurityMockMvcConfigurers.springSecurity())
                .build();
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        noticeBoard = NoticeFactory.noticeBoard();
        place = PlaceFactory.place();
        placeImage = PlaceFactory.placeImage();
        boardResponseDto = BoardFactory.boardResponse();
        responseDto = NoticeFactory.response();
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    //메인 페이지 작동 확인
    @Test
    @DisplayName("메인 페이지 테스트")
    public void MainPageTest()throws Exception{
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());

        List<BoardResponse>boardList = new ArrayList<>();
        List<NoticeResponse>noticeList = new ArrayList<>();

        boardList.add(boardResponseDto);
        noticeList.add(responseDto);

        Page<BoardResponse>boardResponses = boardService.listFreeBoard(pageRequest);
        Page<NoticeResponse>response =  noticeService.listNoticeBoard(pageRequest);

        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(boardRepository.boardList(pageRequest)).willReturn(boardResponses);
        given(noticeBoardRepository.findAllList(pageRequest)).willReturn(response);
        given(placeRepository.placeTop5(pageRequest)).willReturn(Page.empty());
        given(placeRepository.findPlaceByLatLng(member.getMemberLat(),member.getMemberLng())).willReturn(anyList());


        mvc.perform(get("/page/main/main")
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }
}

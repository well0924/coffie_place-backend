package com.example.coffies_vol_02.TestFavoritePlace;

import com.example.coffies_vol_02.Factory.BoardFactory;
import com.example.coffies_vol_02.Factory.CommentFactory;
import com.example.coffies_vol_02.Factory.MemberFactory;
import com.example.coffies_vol_02.Factory.PlaceFactory;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.place.domain.Place;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class FavoriteControllerTest {

    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;

    @MockBean
    private FavoritePlaceService favoritePlaceService;

    Member member;

    Board board;

    Place place;

    MemberResponse memberResponseDto;

    BoardResponse boardResponseDto;

    placeCommentResponseDto responseDto;

    private CustomUserDetails customUserDetails;

    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();

    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = MemberFactory.memberDto();
        board = BoardFactory.board();
        place = PlaceFactory.place();
        memberResponseDto = MemberFactory.response();
        boardResponseDto = BoardFactory.boardResponse();
        responseDto = CommentFactory.placeCommentResponseDto();
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("내가 작성한 글 페이지")
    public void myContentPage()throws Exception{
        Pageable pageable = PageRequest.of(0,5, Sort.by("id").descending());
        List<BoardResponse> list = new ArrayList<>();
        list.add(boardResponseDto);
        Page<BoardResponse> result = new PageImpl<>(list,pageable,1);
        given(favoritePlaceService.getMyPageBoardList(pageable, member.getUserId())).willReturn(result);

        when(favoritePlaceService.getMyPageBoardList(pageable, member.getUserId())).thenReturn(result);
        mvc.perform(get("/page/mypage/contents/{id}",member.getUserId())
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService).getMyPageBoardList(any(), any());
    }

    @Test
    @DisplayName("내가 작성한 댓글 페이지")
    public void myCommentPage()throws Exception{
        Pageable pageable = PageRequest.of(0,5, Sort.by("id").descending());
        List<placeCommentResponseDto> list = new ArrayList<>();
        list.add(responseDto);

        given(favoritePlaceService.getMyPageCommnetList(member.getUserId(),pageable)).willReturn(list);

        when(favoritePlaceService.getMyPageCommnetList(member.getUserId(),pageable)).thenReturn(list);

        mvc.perform(get("/page/mypage/comment/{id}",member.getUserId())
                .with(user(customUserDetails))
                .contentType(MediaType.TEXT_HTML)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService).getMyPageCommnetList(any(),any());
    }

    @Test
    @DisplayName("마이 위시리스트 페이지")
    public void myWishPage()throws Exception{

        mvc.perform(get("/page/mypage/page/{user_id}",member.getUserId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.TEXT_HTML)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

}

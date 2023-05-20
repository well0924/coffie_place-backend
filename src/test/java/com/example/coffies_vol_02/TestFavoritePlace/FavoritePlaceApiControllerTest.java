package com.example.coffies_vol_02.TestFavoritePlace;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponseDto;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.TestCustomUserDetailsService;
import com.example.coffies_vol_02.config.security.auth.CustomUserDetails;
import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.favoritePlace.repository.FavoritePlaceRepository;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.MemberDto;
import com.example.coffies_vol_02.member.repository.MemberRepository;
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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@SpringBootTest
@AutoConfigureMockMvc
public class FavoritePlaceApiControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private WebApplicationContext context;
    @MockBean
    private FavoritePlaceService favoritePlaceService;
    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private BoardRepository boardRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private PlaceRepository placeRepository;
    private Member member;
    private Board board;
    private Place place;
    private PlaceImage placeImage;
    private Comment comment;
    private FavoritePlace favoritePlace;
    private List<PlaceImage>placeImageList = new ArrayList<>();
    MemberDto.MemberResponseDto memberResponseDto;
    BoardResponseDto boardResponseDto;
    CommentDto.CommentResponseDto commentResponseDto;
    FavoritePlaceDto.FavoriteResponseDto favoriteResponseDto;
    private CustomUserDetails customUserDetails;
    private final TestCustomUserDetailsService testCustomUserDetailsService = new TestCustomUserDetailsService();


    @BeforeEach
    public void init(){
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
        member = memberDto();
        board = board();
        comment = comment();
        place = place();
        favoritePlace = favoritePlace();
        placeImage = placeImage();
        placeImageList.add(placeImage());
        memberResponseDto =responseDto();
        boardResponseDto = boardResponseDto();
        commentResponseDto = commentResponseDto();
        favoriteResponseDto = favoriteResponseDto();
        customUserDetails = (CustomUserDetails) testCustomUserDetailsService.loadUserByUsername(member.getUserId());
    }

    @Test
    @DisplayName("내가 작성한 게시글")
    public void boardListTest() throws Exception {
        List<Board>list = new ArrayList<>();
        List<BoardResponseDto> boardList = new ArrayList<>();
        boardList.add(boardResponseDto);
        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        Page<Board>boardPage = new PageImpl<>(list,pageRequest,1);
        Page<BoardResponseDto> pageBoardList = new PageImpl<>(boardList,pageRequest,1);

        System.out.println(pageBoardList.toList());

        given(memberRepository.findByUserId(eq(member.getUserId()))).willReturn(Optional.of(member));
        given(boardRepository.findByMember(eq(member),eq(pageRequest))).willReturn(boardPage);

        when(favoritePlaceService.getMyPageBoardList(eq(pageRequest), eq(member.getUserId()))).thenReturn(pageBoardList);

        mvc.perform(get("/api/mypage/contents/{id}",member.getUserId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService).getMyPageBoardList(any(), any());
    }

    @Test
    @DisplayName("내가 작성한 댓글")
    public void myCommentListTest() throws Exception {

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<Comment>list = new ArrayList<>();
        list.add(comment);
        List<CommentDto.CommentResponseDto>result = new ArrayList<>();
        result.add(commentResponseDto);

        given(memberRepository.findByUserId(eq(member.getUserId()))).willReturn(Optional.of(member));
        given(commentRepository.findByMember(eq(member),eq(pageRequest))).willReturn(list);

        when(favoritePlaceService.getMyPageCommnetList(eq(member.getUserId()),eq(pageRequest))).thenReturn(result);

        mvc.perform(get("/api/mypage/comment/{id}",member.getUserId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    @DisplayName("위시리스트 목록")
    public void wishListTest()throws Exception{

        Pageable pageable = PageRequest.of(0,5,Sort.by("id").descending());
        List<FavoritePlaceDto.FavoriteResponseDto>list = new ArrayList<>();
        list.add(favoriteResponseDto());
        Page<FavoritePlaceDto.FavoriteResponseDto>result = new PageImpl<>(list,pageable,1);

        given(memberRepository.findByUserId(eq(member.getUserId()))).willReturn(Optional.of(member));
        given(favoritePlaceRepository.favoritePlaceWishList(eq(pageable),eq(member.getUserId()))).willReturn(result);

        when(favoritePlaceService.MyWishList(eq(pageable),eq(member.getUserId()))).thenReturn(result);

        mvc.perform(get("/api/mypage/{user_id}",member.getUserId())
                        .with(user(customUserDetails))
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService,times(1)).MyWishList(eq(pageable),eq(member.getUserId()));
    }

    @Test
    @DisplayName("위시리스트 중복체크")
    public void wishListCheckTest()throws Exception{
        given(memberRepository.findById(eq(member.getId()))).willReturn(Optional.of(member));
        given(placeRepository.findById(eq(place.getId()))).willReturn(Optional.of(place));
        given(favoritePlaceRepository.existsByPlaceIdAndMemberId(eq(place.getId()),eq(member.getId()))).willReturn(false);

        when(favoritePlaceService.hasWishPlace(eq(place.getId()),eq(member.getId()))).thenReturn(false);

        mvc.perform(get("/api/mypage/check/{member_id}/{place_id}",member.getUserId(),place.getId())
                .with(user(customUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService).hasWishPlace(any(),any());
    }

    @Test
    @DisplayName("위시리스트 추가")
    public void wishListAddTest()throws Exception{

        given(memberRepository.findById(eq(member.getId()))).willReturn(Optional.of(member));
        given(placeRepository.findById(eq(place.getId()))).willReturn(Optional.of(place));
        given(favoritePlaceRepository.save(eq(favoritePlace))).willReturn(favoritePlace);

        doNothing().when(favoritePlaceService).wishListAdd(eq(member.getId()),eq(place.getId()));

        mvc.perform(post("/api/mypage/{member_id}/{place_id}",member.getId(),place.getId())
                .with(user(customUserDetails))
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService).wishListAdd(eq(member.getId()),eq(place.getId()));
    }

    @Test
    @DisplayName("위시리스트 삭제")
    public void wishListDeleteTest()throws Exception{
        given(favoritePlaceRepository.findById(eq(favoritePlace.getId()))).willReturn(Optional.of(favoritePlace));

        doNothing().when(favoritePlaceService).wishDelete(eq(favoritePlace.getId()));

        mvc.perform(delete("/api/mypage/delete/{place_id}",place.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .characterEncoding(StandardCharsets.UTF_8)
                .with(user(customUserDetails)))
                .andExpect(status().is2xxSuccessful())
                .andDo(print());

        verify(favoritePlaceService).wishDelete(eq(favoritePlace.getId()));
    }

    private Member memberDto(){
        return Member
                .builder()
                .id(1)
                .userId("well4149")
                .password("qwer4149!!")
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userAge("20")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .build();
    }
    private Board board(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(memberDto().getUserId())
                .boardTitle("test")
                .boardContents("test!!")
                .fileGroupId("free_sve345s")
                .readCount(0)
                .passWd("1234")
                .member(memberDto())
                .build();
    }
    private Comment comment(){
        return Comment
                .builder()
                .replyContents("reply test")
                .replyWriter(member.getUserId())
                .replyPoint(3)
                .board(board)
                .member(member)
                .place(place)
                .build();
    }

    private Place place(){
        return Place
                .builder()
                .id(1)
                .placeLng(123.3443)
                .placeLat(23.34322)
                .placeAddr1("xxxx시 xx구")
                .placeAddr2("ㅁㄴㅇㄹ")
                .placeStart("09:00")
                .placeClose("18:00")
                .placeAuthor("admin")
                .placePhone("010-3444-3654")
                .reviewRate(0.0)
                .fileGroupId("place_fre353")
                .placeName("test place1")
                .placeImages(placeImageList)
                .build();
    }

    private PlaceImage placeImage(){
        return PlaceImage
                .builder()
                .fileGroupId("place_ereg34593")
                .thumbFilePath("C:\\\\UploadFile\\\\coffieplace\\images\\thumb\\file_1320441223849700_thumb.jpg")
                .thumbFileImagePath("/istatic/images/coffieplace/images/thumb/1320441218420200_thumb.jpg")
                .imgPath("C:\\\\UploadFile\\\\coffieplace\\images\\1320441218420200.jpg")
                .storedName("다운로드 (1).jpg")
                .originName("1320441218420200.jpg")
                .imgUploader(member.getUserId())
                .imgGroup("coffieplace")
                .isTitle("1")
                .build();
    }

    private FavoritePlace favoritePlace(){
        return FavoritePlace
                .builder()
                .id(1)
                .place(place())
                .fileGroupId(place().getFileGroupId())
                .member(memberDto())
                .build();
    }

    private FavoritePlaceDto.FavoriteResponseDto favoriteResponseDto(){
        return FavoritePlaceDto.FavoriteResponseDto
                .builder()
                .favoritePlace(favoritePlace)
                .build();
    }

    private MemberDto.MemberResponseDto responseDto(){
        return MemberDto.MemberResponseDto
                .builder()
                .id(1)
                .userId("well4149")
                .password(memberDto().getPassword())
                .memberName("userName")
                .userEmail("well414965@gmail.com")
                .userPhone("010-9999-9999")
                .userGender("남자")
                .userAddr1("xxxxxx시 xxxx")
                .userAddr2("ㄴㅇㄹㅇㄹㅇ")
                .role(Role.ROLE_ADMIN)
                .createdTime(LocalDateTime.now())
                .updatedTime(LocalDateTime.now())
                .build();
    }

    private BoardResponseDto boardResponseDto(){
        return BoardResponseDto.builder()
                .id(board().getId())
                .boardTitle(board().getBoardTitle())
                .boardAuthor(board().getBoardAuthor())
                .boardContents(board().getBoardContents())
                .fileGroupId(board().getFileGroupId())
                .readCount(board().getReadCount())
                .passWd(board().getPassWd())
                .updatedTime(LocalDateTime.now())
                .createdTime(LocalDateTime.now())
                .build();
    }

    private CommentDto.CommentResponseDto commentResponseDto(){
        return CommentDto.CommentResponseDto
                .builder()
                .comment(comment())
                .build();
    }

}

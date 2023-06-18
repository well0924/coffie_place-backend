package com.example.coffies_vol_02.TestFavoritePlace;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import com.example.coffies_vol_02.favoritePlace.repository.FavoritePlaceRepository;
import com.example.coffies_vol_02.favoritePlace.service.FavoritePlaceService;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.domain.Role;
import com.example.coffies_vol_02.member.domain.dto.response.MemberResponse;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.PlaceImage;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FavoritePlaceServiceTest {

    @InjectMocks
    private FavoritePlaceService favoritePlaceService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private PlaceRepository placeRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private FavoritePlaceRepository favoritePlaceRepository;

    private Member member;

    private Board board;

    private Comment comment;

    private Place place;

    private PlaceImage placeImage;

    private FavoritePlace favoritePlace;

    MemberResponse memberResponseDto;

    BoardResponse boardResponseDto;

    placeCommentResponseDto ResponseDto;

    FavoritePlaceResponseDto favoritePlaceResponseDto;

    List<FavoritePlace>list = new ArrayList<>();

    List<PlaceImage> placeImages = new ArrayList<>();

    @BeforeEach
    public void init() throws Exception {
        member = memberDto();
        comment = comment();
        board = board();
        place = place();
        placeImage = placeImage();
        favoritePlace = favoritePlace();
        list.add(favoritePlace());
        placeImages.add(placeImage());
        memberResponseDto = responseDto();
        boardResponseDto = boardResponseDto();
        ResponseDto = commentResponseDto();
        favoritePlaceResponseDto = favoritePlaceResponseDto();
    }

    @Test
    @DisplayName("내가 작성한 글 조회")
    public void MyPageBoardListTest(){

        List<Board> boardList = new ArrayList<>();

        boardList.add(board);

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());

        Page<Board> pageBoardList = new PageImpl<>(boardList,pageRequest,1);

        given(memberRepository.findByUserId(member.getUserId())).willReturn(Optional.of(member));
        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(boardRepository.findByMember(member,pageRequest)).willReturn(pageBoardList);

        Page<BoardResponse>result = favoritePlaceService.getMyPageBoardList(pageRequest, member.getUserId());

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("내가 작성한 글 조회-실패")
    public void MyPageBoardListTestFail(){

        List<Board> boardList = new ArrayList<>();

        boardList.add(board);

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());

        Page<Board> pageBoardList = new PageImpl<>(boardList,pageRequest,1);

        given(boardRepository.findById(board.getId())).willReturn(Optional.of(board));
        given(boardRepository.findByMember(member,pageRequest)).willReturn(pageBoardList);

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()->{
            Page<BoardResponse>result = favoritePlaceService.getMyPageBoardList(pageRequest, member.getUserId());
        });

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }

    @Test
    @DisplayName("내가 작성한 댓글 조회")
    public void MyPageCommentListTest(){

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());

        List<Comment>list = new ArrayList<>();

        list.add(comment);

        given(memberRepository.findByUserId(member.getUserId())).willReturn(Optional.of(member));
        given(commentRepository.findByMember(member,pageRequest)).willReturn(list);

        List<placeCommentResponseDto>result = favoritePlaceService.getMyPageCommnetList(member.getUserId(),pageRequest);

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("내가 작성한 댓글 조회-실패")
    public void MyPageCommentListTestFail(){

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());

        List<Comment>list = new ArrayList<>();

        list.add(comment);

        given(commentRepository.findByMember(member,pageRequest)).willReturn(list);

        CustomExceptionHandler customExceptionHandler = assertThrows(CustomExceptionHandler.class,()-> {
            List<placeCommentResponseDto> result = favoritePlaceService.getMyPageCommnetList(member.getUserId(), pageRequest);
        });

        assertThat(customExceptionHandler.getErrorCode()).isEqualTo(ERRORCODE.ONLY_USER);
    }
    
    @Test
    @DisplayName("위시리스트 추가하기")
    public void wishListAddTest(){
        //given
        given(memberRepository.findById(anyInt())).willReturn(Optional.of(member));
        given(placeRepository.findById(anyInt())).willReturn(Optional.of(place));
        given(favoritePlaceRepository.save(favoritePlace())).willReturn(favoritePlace);
        //when
        favoritePlaceService.wishListAdd(member.getId(),place.getId());
        //then
        boolean result = favoritePlaceService.hasWishPlace(place.getId(),member.getId());
        assertThat(result).isFalse();
    }
    
    @Test
    @DisplayName("위시리스트 삭제하기.")
    public void wishListDeleteTest(){
        given(memberRepository.findById(member.getId())).willReturn(Optional.of(member));
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(favoritePlaceRepository.findById(favoritePlace.getId())).willReturn(Optional.of(favoritePlace));

        favoritePlaceService.wishDelete(favoritePlace.getId());

        verify(favoritePlaceRepository).delete(eq(favoritePlace));
    }
    
    @Test
    @DisplayName("위시리스트 목록.")
    public void wishListTest(){
        //given
        given(memberRepository.findByUserId(member.getUserId())).willReturn(Optional.of(member));
        given(placeRepository.findById(place.getId())).willReturn(Optional.of(place));
        given(favoritePlaceRepository.save(favoritePlace())).willReturn(favoritePlace);

        PageRequest pageRequest= PageRequest.of(0,5, Sort.by("id").descending());
        List<FavoritePlaceResponseDto>re = new ArrayList<>();
        re.add(favoritePlaceResponseDto);
        Page<FavoritePlaceResponseDto>result = new PageImpl<>(re,pageRequest,0);

        //when
        result = favoritePlaceService.MyWishList(pageRequest,member.getUserId());
        //then
        then(favoritePlaceRepository.favoritePlaceWishList(pageRequest,member.getUserId()));

        verify(favoritePlaceRepository,atLeastOnce()).favoritePlaceWishList(pageRequest,member.getUserId());
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
    private Board board(){
        return Board
                .builder()
                .id(1)
                .boardAuthor(member.getUserId())
                .boardTitle("test")
                .boardContents("test!")
                .passWd("132v")
                .readCount(0)
                .fileGroupId("free_weft33")
                .member(member)
                .build();
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
                .placeImages(placeImages)
                .build();
    }
    private FavoritePlace favoritePlace(){
        return FavoritePlace
                .builder()
                .id(1)
                .place(place())
                .member(memberDto())
                .fileGroupId(place.getFileGroupId())
                .build();
    }
    private MemberResponse responseDto(){
        return new MemberResponse(member);
    }
    private BoardResponse boardResponseDto(){
        return new BoardResponse(board);
    }
    private placeCommentResponseDto commentResponseDto(){
        return placeCommentResponseDto
                .builder()
                .comment(comment)
                .build();
    }
    private FavoritePlaceResponseDto favoritePlaceResponseDto(){
        return FavoritePlaceResponseDto
                .builder()
                .favoritePlace(favoritePlace)
                .build();
    }
}

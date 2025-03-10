package com.example.coffies_vol_02.favoritePlace.service;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.example.coffies_vol_02.commnet.repository.CommentRepository;
import com.example.coffies_vol_02.config.api.service.KakaoApiSearchService;
import com.example.coffies_vol_02.config.constant.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.favoritePlace.domain.dto.FavoritePlaceResponseDto;
import com.example.coffies_vol_02.favoritePlace.repository.FavoritePlaceRepository;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.domain.dto.response.PlaceResponseDto;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@Transactional
@AllArgsConstructor
public class FavoritePlaceService {

    private final BoardRepository boardRepository;

    private final MemberRepository memberRepository;

    private final CommentRepository commentRepository;

    private final PlaceRepository placeRepository;

    private final FavoritePlaceRepository favoritePlaceRepository;

    private final KakaoApiSearchService apiSearchService;

    /**
     * 위시 리스트 목록
     * @author 양경빈
     * @param pageable 페이징 하기 위한 객체
     * @param userId 회원 아이디
     * @return Page<FavoritePlaceResponseDto>
     * @see FavoritePlaceRepository#favoritePlaceWishList(Pageable, String) 로그인을 한 회원이 마이페이지에서 위시리스트를 확인하는 메서드
     **/
    @Transactional(readOnly = true)
    public Page<FavoritePlaceResponseDto>MyWishList(Pageable pageable, String userId){
        return favoritePlaceRepository.favoritePlaceWishList(pageable,userId);
    }

    /**
     * 위시 리스트 중복처리
     * @author 양경빈
     * @param placeId 가게 번호
     * @param memberId 회원 아이디
     * @return boolean 중복이 되면 true 중복이 아니면 false
     * @see FavoritePlaceRepository#existsByPlaceIdAndMemberUserId(Integer, String)  위시리스트에서 목록이 있는지를 확인하는 메서드
     **/
    public boolean hasWishPlace(Integer placeId,String memberId){
        return favoritePlaceRepository.existsByPlaceIdAndMemberUserId(placeId, memberId);
    }

    /**
     * 위시리스트 추가
     * @author 양경빈
     * @param memberId 회원 번호 죄회되는 번호가 없는 경우에는 NOT_FOUND_MEMBER 발생
     * @param placeId 가게 번호 조회되는 번호가 없는 경우에는 PLACE_NOT_FOUND 발생
     * @see MemberRepository#findById(Object) 회원번호로 회원 단일 조회하는 메서드 조회하는 번호가 없는 경우에는 NOT_FOUND_MEMBER 발생
     * @see PlaceRepository#findById(Object) 가게번호로 가게 단일 조회하는 메서드 조회하는 번호가 없는 경우에는 PLACE_NOT_FOUND 발생
     * @see FavoritePlaceRepository#save(Object) 위시리스트를 저장하는 메서드
     **/
    public void wishListAdd(String memberId, Integer placeId){
        Optional<Member>member = Optional.of(memberRepository.findByUserId(memberId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        log.info(member.get());

        Optional<Place>place = Optional.ofNullable(placeRepository.findById(placeId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        log.info(place.get());

        favoritePlaceRepository.save(FavoritePlace
                .builder()
                .member(member.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)))
                .place(place.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)))
                .build());
    }

    /**
     * 위시 리스트 삭제
     * @author 양경빈
     * @param wishId 위시리스트 번호 조회하는 번호가 없는 경우에는 NOT_WISHLIST
     * @exception CustomExceptionHandler 위시리스트가 없는 경우
     * @see FavoritePlaceRepository#findById(Object) 위시리스트 번호로 위시리스트를 단일 조회 조회할 번호가 없는 경우에는 NOT_WISHLIST
     * @see FavoritePlaceRepository#delete(Object) 위시리스트를 삭제하는 메서드
     **/
    public void wishDelete(Integer wishId){
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(wishId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_WISHLIST));

        favoritePlaceRepository.delete(favoritePlace);
    }

    /**
     * 내가 작성한 글 확인하기
     * @author 양경빈
     * @param pageable 페이징 객체
     * @param userId 회원 아이디 회원 아이디가 없는 경우에는 ONLY_USER 가 발생
     * @exception CustomExceptionHandler 회원 아이디가 없는 경우에는 ONLY_USER 가 발생
     * @return Page<BoardResponse>
     * @see MemberRepository#findByUserId(String) 시큐리티 로그인 인증 메서드
     * @see BoardRepository#findByMember(Member, Pageable) 회원이 작성을 한 글을 마이페이지에서 확인하는 메서드
     **/
    @Transactional(readOnly = true)
    public Page<BoardResponse> getMyPageBoardList(Pageable pageable, String userId){
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.ONLY_USER));

        Page<Board>list = boardRepository.findByMember(member,pageable);

        return list.map(BoardResponse::new);
    }

    /**
     * 내가 작성한 댓글
     * @author 양경빈
     * @param userId 회원 아이디
     * @param pageable 페이지 객체
     * @return list 내가 작성한 댓글 리스트
     * @see MemberRepository#findByUserId(String) 회원 시큐리티 로그인 메서드
     * @see CommentRepository#findByMember(Member, Pageable) 로그인한 회원이 작성한 댓글을 확인하는 메서드
     **/
    @Transactional(readOnly = true)
    public List<placeCommentResponseDto> getMyPageCommnetList(String userId, Pageable pageable){
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.ONLY_USER));

        List<Comment>list = commentRepository.findByMember(member,pageable);

        return list.stream().map(placeCommentResponseDto::new).collect(Collectors.toList());
    }

    /**
     * 가까운 가게 5곳
     * 회원의 위경도를 기준으로 가까운 가게 5곳을 보여주는 기능
     * @return result 가게 정보 dto 값
     **/
    @Transactional(readOnly = true)
    public List<PlaceResponseDto>findByMemberNearList(Member member){
        List<String> placeNames = apiSearchService.extractPlaceName(member);
        return placeRepository.findPlacesByName(placeNames)
                .stream()
                .map(PlaceResponseDto::new)
                .collect(Collectors.toList());
    }

    /**
     * 회원이 좋아요를 한 게시글 목록
     * @author 양경빈
     * @param userIdx 회원 번호
     * @return Page<BoardResponse>result 회원의 게시글 목록
     **/
    @Transactional(readOnly = true)
    public Page<BoardResponse>likedBoardList(int userIdx,Pageable pageable){
        return boardRepository.likedBoardDetailList(userIdx,pageable);
    }
}

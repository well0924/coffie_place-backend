package com.example.coffies_vol_02.FavoritePlace.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.FavoritePlace.domain.FavoritePlace;
import com.example.coffies_vol_02.FavoritePlace.domain.dto.FavoritePlaceDto;
import com.example.coffies_vol_02.FavoritePlace.repository.FavoritePlaceRepository;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import com.example.coffies_vol_02.Place.domain.Place;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavoritePlaceService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final PlaceRepository placeRepository;
    private final FavoritePlaceRepository favoritePlaceRepository;

    /*
     * 
     * 위시 리스트 목록
     */
    public List<FavoritePlaceDto.FavoriteResponseDto>findByMemberId(String userId){
        Optional<Member>member = Optional.of(memberRepository.findByUserId(userId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER)));
        List<FavoritePlace>wishList = member.get().getWishList();
        return wishList.stream().map(FavoritePlaceDto.FavoriteResponseDto::new).collect(Collectors.toList());
    }

    /*
     * 위시 리스트 중복처리
     */
    public boolean hasWishPlace(Integer placeId,Integer memberId){
        return favoritePlaceRepository.existsByPlaceIdAndMemberId(placeId, memberId);
    }

    /*
     * 위시리스트 추가
     */
    public void wishListAdd(Integer memberId,Integer placeId){
        Optional<Member>member = Optional.of(memberRepository.findById(memberId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER)));
        Optional<Place>place = Optional.ofNullable(placeRepository.findById(placeId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        favoritePlaceRepository.save(FavoritePlace
                .builder()
                .member(member.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_MEMBER)))
                .place(place.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)))
                .build());
    }

    /*
    * 위시 리스트 삭제
    */
    public void wishDelete(Integer wishId){
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(wishId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_WISHLIST));
        favoritePlaceRepository.delete(favoritePlace);
    }

    /*
     * 내가 작성한 글 확인하기.
     */
    public Page<BoardDto.BoardResponseDto> getMyPageBoardList(Pageable pageable, String userId){
        Member member = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.ONLY_USER));
        Page<Board>list = boardRepository.findByMember(member,pageable);
        return list.map(BoardDto.BoardResponseDto::new);
    }

    /*
     * 내가 작성한 댓글
     */
    public List<CommentDto.CommentResponseDto> getMyPageCommnetList(String userId,Pageable pageable){
        Member member = memberRepository.findByUserId(userId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.ONLY_USER));
        List<Comment>list = commentRepository.findByMember(member,pageable);
        return list.stream().map(CommentDto.CommentResponseDto::new).collect(Collectors.toList());
    }

}

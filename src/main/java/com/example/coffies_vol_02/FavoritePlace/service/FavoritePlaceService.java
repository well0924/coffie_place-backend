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
import com.example.coffies_vol_02.Place.domain.dto.PlaceDto;
import com.example.coffies_vol_02.Place.repository.PlaceRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                                        .member(member.get())
                                        .place(place.get())
                                        .build());
    }

    /*
    * 위시 리스트 삭제
    */
    public void wishDelete(Integer wishId){
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(wishId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_WISHLIST));
        favoritePlaceRepository.delete(favoritePlace);
    }
    public void deleteById(Integer placeId,Integer memberId){
        favoritePlaceRepository.deleteByPlaceIdAndMemberId(placeId,memberId);
    }
    /*
     * 내가 작성한 글 확인하기.
     */
    public Page<BoardDto.BoardResponseDto> getMyPageBoardList(Pageable pageable, Member member,String userId){
        member = memberRepository.findByUserId(userId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.ONLY_USER));
        Page<Board>list = boardRepository.findByMember(member,pageable);
        return list.map(board -> new BoardDto.BoardResponseDto(board));
    }

    /*
     * 내가 작성한 댓글
     */
    public List<CommentDto.CommentResponseDto> getMyPageCommnetList(String userId,Pageable pageable, Member member){
        member = memberRepository.findByUserId(userId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.ONLY_USER));
        List<Comment>list = commentRepository.findByMember(member,pageable);
        return list.stream().map(comment -> new CommentDto.CommentResponseDto(comment)).collect(Collectors.toList());
    }

}

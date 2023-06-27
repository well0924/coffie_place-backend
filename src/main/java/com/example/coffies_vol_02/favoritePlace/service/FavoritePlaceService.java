package com.example.coffies_vol_02.favoritePlace.service;

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
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.member.repository.MemberRepository;
import com.example.coffies_vol_02.place.domain.Place;
import com.example.coffies_vol_02.place.repository.PlaceRepository;
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

    public Page<FavoritePlaceResponseDto>MyWishList(Pageable pageable, String userId){
        return favoritePlaceRepository.favoritePlaceWishList(pageable,userId);
    }

    public boolean hasWishPlace(Integer placeId,Integer memberId){
        return favoritePlaceRepository.existsByPlaceIdAndMemberId(placeId, memberId);
    }

    public void wishListAdd(Integer memberId,Integer placeId){
        Optional<Member>member = Optional.of(memberRepository.findById(memberId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)));

        Optional<Place>place = Optional.ofNullable(placeRepository.findById(placeId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)));

        favoritePlaceRepository.save(FavoritePlace
                .builder()
                .member(member.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_FOUND_MEMBER)))
                .place(place.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.PLACE_NOT_FOUND)))
                .build());
    }

    public void wishDelete(Integer wishId){
        FavoritePlace favoritePlace = favoritePlaceRepository.findById(wishId)
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_WISHLIST));

        favoritePlaceRepository.delete(favoritePlace);
    }

    public Page<BoardResponse> getMyPageBoardList(Pageable pageable, String userId){
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.ONLY_USER));

        Page<Board>list = boardRepository.findByMember(member,pageable);

        return list.map(BoardResponse::new);
    }

    public List<placeCommentResponseDto> getMyPageCommnetList(String userId, Pageable pageable){
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.ONLY_USER));

        List<Comment>list = commentRepository.findByMember(member,pageable);

        return list.stream().map(placeCommentResponseDto::new).collect(Collectors.toList());
    }

}

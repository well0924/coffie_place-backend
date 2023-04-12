package com.example.coffies_vol_02.FavoritePlace.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.FavoritePlace.repository.FavoritePlaceRepository;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FavoritePlaceService {
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;

    /*
     * 
     * 위시 리스트 목록
     */
    
    /*
     * 
     * 위시 리스트 삭제 
     */

    /*
     *
     * 내가 작성한 글 확인하기.
     */
    public Page<BoardDto.BoardResponseDto> getMyPageBoardList(Pageable pageable, Member member,String userId){
        member = memberRepository.findByUserId(userId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.ONLY_USER));
        Page<Board>list = boardRepository.findByMember(member,pageable);
        return list.map(board -> new BoardDto.BoardResponseDto(board));
    }

    /*
     *
     * 내가 작성한 댓글
     */
    public List<CommentDto.CommentResponseDto> getMyPageCommnetList(String userId,Pageable pageable, Member member){
        member = memberRepository.findByUserId(userId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.ONLY_USER));
        List<Comment>list = commentRepository.findByMember(member,pageable);
        return list.stream().map(comment -> new CommentDto.CommentResponseDto(comment)).collect(Collectors.toList());
    }
}

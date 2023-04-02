package com.example.coffies_vol_02.Commnet.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Commnet.domain.dto.CommentDto;
import com.example.coffies_vol_02.Commnet.repository.CommentRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.Member;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    /*
    *   댓글 목록 
    */
    @Transactional(readOnly = true)
    public List<CommentDto.CommentResponseDto> replyList(Integer boardId) throws Exception {
        List<Comment>list = commentRepository.findCommentsBoardId(boardId);

        if(list.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.NOT_REPLY);
        }

        return list.stream().map(comment -> new CommentDto.CommentResponseDto()).collect(Collectors.toList());
    }
    /*
    *   댓글 작성
    */
    @Transactional
    public Integer replyWrite(Integer boardId,Member member,CommentDto.CommentRequestDto dto){
        Optional<Board>boarddetail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        Comment comment = Comment
                .builder()
                .board(boarddetail.get())
                .replyWriter(member.getUserId())
                .replyContents(dto.getReplyContents())
                .build();

        commentRepository.save(comment);

        return comment.getId();
    }

    /*
    *   댓글 삭제
    */
    @Transactional
    public void commentDelete(Integer replyId,Member member){
        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }
        Comment comment = commentRepository.findById(replyId).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.NOT_REPLY));
        String userId = member.getUserId();
        String commentAuthor = comment.getReplyWriter();

        if(!userId.equals(commentAuthor)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }
        commentRepository.deleteById(replyId);
    }
}

package com.example.coffies_vol_02.Board.service;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    /*
    * 게시글 목록
    */
    @Transactional(readOnly = true)
    public Page<BoardDto.BoardResponseDto> boardAll(Pageable pageable){
        Page<Board>list = boardRepository.findAll(pageable);

        if(list.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST);
        }
        return list.map(board -> new BoardDto.BoardResponseDto(board));
    }

    /*
    *  게시글 단일 조회
    *
    */
    @Transactional(readOnly = true)
    public BoardDto.BoardResponseDto boardDetail(Integer boardId){
        Optional<Board> boardDetail= Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Board result = boardDetail.get();
        result.countUp();

        return BoardDto.BoardResponseDto.builder()
                .id(result.getId())
                .boardAuthor(result.getBoardAuthor())
                .boardTitle(result.getBoardTitle())
                .boardContents(result.getBoardContents())
                .readCount(result.getReadCount())
                .passWd(result.getPassWd())
                .fileGroupId(result.getFileGroupId())
                .createdTime(result.getCreatedTime())
                .updatedTime(result.getUpdatedTime())
                .build();
    }

    /*
    * 글작성
    *
    */
    @Transactional
    public Integer boardSave(BoardDto.BoardRequestDto requestDto, Member member){

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Board board = Board
                .builder()
                .boardTitle(requestDto.getBoardTitle())
                .boardContents(requestDto.getBoardContents())
                .readCount(requestDto.getReadCount())
                .passWd(requestDto.getPassWd())
                .fileGroupId(requestDto.getFileGroupId())
                .member(member)
                .build();
        boardRepository.save(board);
        return board.getId();
    }

    /*
    *  게시글 수정
    */
    @Transactional
    public Integer BoardUpdate(Integer boardId, BoardDto.BoardRequestDto dto, Member member){
        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        String boardAuthor = detail.get().getBoardAuthor();
        String userId = member.getUserId();

        if(!boardAuthor.equals(userId)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }
        detail.get().boardUpdate(dto);
        return detail.get().getId();
    }

    /*
    *  게시글 삭제
    */
    @Transactional
    public void BoardDelete(Integer boardId,Member member){

        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        Board  board = detail.get();
        String boardAuthor = board.getBoardAuthor();
        String userId = member.getUserId();

        if(!boardAuthor.equals(userId)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }
        boardRepository.deleteById(boardId);
    }

    /*
    * 비밀번호 확인
    */
    @Transactional
    public BoardDto.BoardResponseDto passwordCheck(String passWd,Integer id,Member member){
        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }
        BoardDto.BoardResponseDto result = boardRepository.findByPassWdAndId(passWd,id);
        if(result ==null){
            throw new CustomExceptionHandler(ERRORCODE.NOT_MATCH_PASSWORD);
        }
        return result;
    }
}

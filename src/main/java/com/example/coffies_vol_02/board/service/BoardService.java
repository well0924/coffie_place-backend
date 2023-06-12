package com.example.coffies_vol_02.board.service;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextPreviousInterface;
import com.example.coffies_vol_02.board.domain.dto.response.BoardResponse;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.board.repository.BoardRepository;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final FileHandler fileHandler;

    private final AttachRepository attachRepository;

    private final AttachService attachService;

    /**
    * 게시글 목록
    **/
    @Transactional(readOnly = true)
    public Page<BoardResponse> boardAllList(Pageable pageable){
        return boardRepository.boardList(pageable);
    }

    /**
    *  게시글 검색
    **/
    @Transactional(readOnly = true)
    public Page<BoardResponse> boardSearchAll(String searchVal, Pageable pageable){
        return boardRepository.findAllSearch(searchVal,pageable);
    }

    /**
    *  게시글 단일 조회
    **/
    @Transactional(readOnly = true)
    public BoardResponse findBoard(Integer boardId){
        BoardResponse result = Optional.ofNullable(boardRepository.boardDetail(boardId)).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
        return result;
    }

    /**
     * 게시글 이전글
     **/
    public BoardNextPreviousInterface findPreviousBoard(Integer boardId){
        BoardNextPreviousInterface result = Optional.ofNullable(boardRepository.findPreviousBoard(boardId)).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
        return result;
    }
    /**
     * 게시글 다음글
     **/
    public BoardNextPreviousInterface findNextBoard(Integer boardId){
        BoardNextPreviousInterface result = Optional.ofNullable(boardRepository.findNextBoard(boardId)).orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
        return result;
    }

    /**
    * 글작성(파일 첨부)
    **/
    @Transactional
    public Integer boardCreate(BoardRequest requestDto, Member member) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Board board = Board
                .builder()
                .boardTitle(requestDto.boardTitle())
                .boardContents(requestDto.boardContents())
                .fileGroupId(requestDto.fileGroupId())
                .readCount(0)
                .passWd(requestDto.passWd())
                .member(member)
                .build();

        boardRepository.save(board);

        Integer InsertResult = board.getId();

        List<Attach>filelist = fileHandler.parseFileInfo(requestDto.files());

        if(filelist == null || filelist.size() == 0){
            return InsertResult;
        }

        for (Attach attachFile : filelist) {
            board.addAttach(attachRepository.save(attachFile));
        }
        return InsertResult;
    }

    /**
    *  게시글 수정(파일 첨부)
    **/
    @Transactional
    public Integer BoardUpdate(Integer boardId, BoardRequest dto, Member member,List<MultipartFile>files) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        Board board = detail.orElse(null);

        String boardAuthor = board.getBoardAuthor();
        String userId = member.getUserId();

        if(!boardAuthor.equals(userId)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        board.boardUpdate(dto);

        Integer UpdateResult = board.getId();

        List<Attach>filelist = attachRepository.findAttachBoard(boardId);

        //파일이 있는 경우에 수정하는 경우
        if(!filelist.isEmpty()){
            for (Attach attach : filelist) {
                String filePath = attach.getFilePath();
                File file = new File(filePath);
                //파일 경로 삭제
                if (file.exists()) file.delete();
                //DB에 있는 파일 삭제
                attachService.deleteBoardAttach(boardId);
            }
            //재업로드
            filelist = fileHandler.parseFileInfo(files);
            //다시 저장
            for(Attach attachFile : filelist){
                detail.get().addAttach(attachRepository.save(attachFile));
            }
        }else{
            //게시글만 작성하고 파일은 첨부를 하지 않은 경우
            //업로드
            filelist = fileHandler.parseFileInfo(files);
            //파일 저장
            for(Attach attachFile : filelist){
                detail.get().addAttach(attachRepository.save(attachFile));
            }
        }
        return UpdateResult;
    }

    /**
    *  게시글 삭제
    **/
    @Transactional
    public void BoardDelete(Integer boardId,Member member) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Board  board = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST));

        String boardAuthor = board.getBoardAuthor();
        String userId = member.getUserId();

        if(!boardAuthor.equals(userId)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        List<AttachDto>attachlist = attachService.boardfilelist(boardId);

        for (AttachDto attachDto : attachlist) {

            String filePath = attachDto.getFilePath();
            File file = new File(filePath);

            if (file.exists()) file.delete();
        }

        boardRepository.deleteById(boardId);
    }

    /**
    * 게시글 비밀번호 확인
    **/
    @Transactional
    public BoardResponse passwordCheck(String passWd,Integer id,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<BoardResponse> boardDetail = Optional.of(Optional.ofNullable(boardRepository.findByPassWdAndId(passWd, id)).orElseThrow(()-> new CustomExceptionHandler(ERRORCODE.NOT_MATCH_PASSWORD)));

        return boardDetail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
    }

}

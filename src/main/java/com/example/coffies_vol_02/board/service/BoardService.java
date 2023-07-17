package com.example.coffies_vol_02.board.service;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequest;
import com.example.coffies_vol_02.board.domain.dto.response.BoardNextInterface;
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
     * @author 양경빈
     * @param pageable 게시물 목록에서 페이징에 필요한 객체
     * @return Page<BoardResponse> 게시물 목록
     **/
    @Transactional(readOnly = true)
    public Page<BoardResponse> boardAllList(Pageable pageable){
        return boardRepository.boardList(pageable);
    }

    /**
     * 게시글 검색
     * @author 양경빈
     * @param searchVal 자유게시물 목록에서 검색에 필요한 검색어
     * @param pageable 게시물 목록에서 페이징에 필요한 객체
     * @return Page<BoardResponse> 게시물 목록
     **/
    @Transactional(readOnly = true)
    public Page<BoardResponse> boardSearchAll(String searchVal, Pageable pageable){
        Page<BoardResponse> result = boardRepository.findAllSearch(searchVal,pageable);
        return result;
    }

    /**
     * 게시물 단일 조회
     * @author 양경빈
     * @param boardId 게시물 번호
     * @return BoardResponse
     **/
    @Transactional(readOnly = true)
    public BoardResponse findBoard(Integer boardId){
        return Optional.ofNullable(boardRepository
                .boardDetail(boardId))
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
    }

    /**
     * 게시글 이전글
     *
     * @param boardId 게시글 번호 번호가 없는 경우에는 BOARD_NOT_FOUND 를 발생
     * @return BoardNextPreviousInterface 타입은 인터페이스
     * @throws CustomExceptionHandler 게시글을 조회시 게시글이 없는 경우
     * @author 양경빈
     * @see BoardRepository#findNextBoard(Integer) 게시글조회 페이지에서 다음글을 조회하는 메서드
     **/
    public Optional<BoardNextPreviousInterface> findPreviousBoard(Integer boardId){

        return Optional
                .ofNullable(boardRepository.findPreviousBoard(boardId))
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
    }

    /**
     * 게시글 다음글
     * @author 양경빈
     * @param boardId 게시글 번호 번호가 없는 경우에는 BOARD_NOT_FOUND
     * @see BoardRepository#findNextBoard(Integer) 게시물 조회페이지에서 다음글을 조회하는 메서드
     * @return BoardNextPreviousInterface 타입은 인터페이스
     **/
    public Optional<BoardNextInterface> findNextBoard(Integer boardId){
        return Optional
                .ofNullable(boardRepository.findNextBoard(boardId))
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
    }

    /**
     * 글작성(파일 첨부)
     * @author 양경빈
     * @param requestDto 게시물에 필요한 dto (게시물 제목과 내용을 작성하지 않은경우에는 유효성 검사)
     * @param member 게시물 작성시 인증에 필요한 객체 로그인을 하지 못한 경우에는 ONLY_USER 발생
     * @exception CustomExceptionHandler 로그인을 하지 않은 경우에는 예외를 발생 ONLY_USER
     * @see FileHandler#parseFileInfo(List) 게시물 작성 및 수정시 파일 업로드를 하는 메서드 파일목록이 없는 경우에는 단순 게시글 작성만 실행
     * @see BoardRepository#save(Object) 게시물 저장
     * @return InsertResult 생성된 게시물 번호
     **/
    @Transactional
    public Integer boardCreate(BoardRequest requestDto,List<MultipartFile>files, Member member) throws Exception {
        
        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        //refactoring 필요
        Board board = Board
                .builder()
                .boardTitle(requestDto.boardTitle())
                .boardContents(requestDto.boardContents())
                .fileGroupId(requestDto.fileGroupId())
                .readCount(0)
                .passWd(requestDto.passWd())
                .member(member)
                .build();

        //게시글 저장
        Integer InsertResult = boardRepository.save(board).getId();;

        //파일  업로드
        List<Attach>filelist = fileHandler.parseFileInfo(files);
        
        //파일이 없는 경우
        if(filelist == null || filelist.size() == 0){
            return InsertResult;
        }
        //첨부 파일이 있는 경우 첨부파일 저장
        if(!filelist.isEmpty()){
            for(Attach attachFile : filelist){
                board.addAttach(attachRepository.save(attachFile));
            }
        }
        
        return InsertResult;
    }

    /**
     * 게시글 수정(파일 첨부)
     * @author 양경빈
     * @param boardId 게시물 번호 게시물 번호가 없는 경우에는 발생 BOARD_NOT_FOUND 발생
     * @param dto 게시물 수정에 필요한  dto
     * @param member 로그인시 인증에 필요한 객체 없는경우에는 ONLY_USER 발생
     * @param files 게시물 수정시 파일 첨부에 필요한 매개변수
     * @exception CustomExceptionHandler 로그인을 안했을 경우,조회하는 게시물이 없는 경우,로그인한 아이디와 작성자 다른 경우(NOT_AUTH)
     * @see BoardRepository#findById(Object) 게시물 단일 조회에 사용되는 메서드
     * @see FileHandler#parseFileInfo(List) 게시물 작성 및 수정을 했을 경우 파일을 업로드 하는 메서드
     * @see AttachRepository#findAttachBoard(Integer)  자유게시글에 있는 첨부파일 목록을 조회하는 메서드
     * @see AttachService#deleteBoardAttach(Integer) 자유게시글에 있는 첨부파일을 삭제하는 메서드
     * @see AttachRepository#save(Object) 첨부파일을 저장하는 메서드
     * @return UpdateResult 수정된 게시글 번호
     **/
    @Transactional
    public Integer BoardUpdate(Integer boardId, BoardRequest dto, Member member,List<MultipartFile>files) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        
        Board board = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

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
     * 게시글 삭제
     * @author 양경빈
     * @param boardId 게시글 번호 번호가 없는 경우에는 BOARD_NOT_FOUND 발생
     * @param member 로그인시 인증에 필요한 객체 로그인이 안된 경우에는 ONLY_USER 발생
     * @exception CustomExceptionHandler 로그인이 안된 경우, 게시물이 없는 경우,로그인한 회원과 작성자가 다른 경우(NOT_AUTH)
     * @see BoardRepository#findById(Object) 게시물을 조회하는 메서드 조회하는 게시글이 없는 경우에는 BOARD_NOT_FOUND
     * @see BoardRepository#deleteById(Object) 게시물을 삭제하는 메서드
     * @see AttachService#boardfilelist(Integer) 자유게시판에 있는 첨부파일 목록을 조회하는 메서드
     * @see AttachRepository#deleteById(Object) 자유게시판에서 첨부파일을 삭제를 할때 사용되는 메서드
     **/
    @Transactional
    public void BoardDelete(Integer boardId,Member member) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId)
                .orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Board  board = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

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
     * @author 양경빈
     * @param passWd 게시판 비밀글에 입력하는 비밀번호 입력한 번호가 맞지않은 경우에는 NOT_MATCH_PASSWORD
     * @param id 게시글 번호 게시글 번호가 없는 경우에는 BOARD_NOT_FOUND
     * @param member 로그인을 인증하는 객체 로그인이 되지 않은 경우에는 ONLY_USER
     * @exception CustomExceptionHandler 로그인을 하지 않은 경우,게시글을 조회하지 않은 경우,비밀번호가 일치하지 않은 경우
     * @see BoardRepository#findById(Object) 게시글을 조회하는 메서드 조회할 게시글이 없는 경우에는 BOARD_NOT_FOUND
     * @see BoardRepository#findByPassWdAndId(String, Integer) 게시글 비밀번호 입력화면에서 비밀번호를 확인하는 메서드 비밀번호가 일치하지 않은 경우에는 NOT_MATCH_PASSWORD
     * @return BoardResponse
     **/
    @Transactional
    public BoardResponse passwordCheck(String passWd,Integer id,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<BoardResponse> boardDetail = Optional
                .of(Optional.ofNullable(boardRepository.findByPassWdAndId(passWd, id))
                .orElseThrow(()-> new CustomExceptionHandler(ERRORCODE.NOT_MATCH_PASSWORD)));

        return boardDetail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
    }

}

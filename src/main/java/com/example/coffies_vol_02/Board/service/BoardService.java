package com.example.coffies_vol_02.Board.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Config.Util.FileHandler;
import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Member.domain.Member;
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
    public Page<BoardDto.BoardResponseDto> boardAll(Pageable pageable){
        return boardRepository.boardList(pageable);
    }

    /**
    *  게시글 검색
    **/
    @Transactional(readOnly = true)
    public Page<BoardDto.BoardResponseDto> boardSearchAll(String searchVal,String sort, Pageable pageable){
        Page<BoardDto.BoardResponseDto>searchResult = boardRepository.findAllSearch(searchVal,sort,pageable);
        return searchResult;
    }

    /**
    *  게시글 단일 조회
    **/
    @Transactional(readOnly = true)
    public BoardDto.BoardResponseDto boardDetail(Integer boardId){
        Optional<Board> boardDetail= Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Board result = boardDetail.get();

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

    /**
    * 글작성(파일 첨부)
    **/
    @Transactional
    public Integer boardSave(BoardDto.BoardRequestDto requestDto, Member member) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Board board = Board
                .builder()
                .boardTitle(requestDto.getBoardTitle())
                .boardContents(requestDto.getBoardContents())
                .readCount(0)
                .passWd(requestDto.getPassWd())
                .fileGroupId(requestDto.getFileGroupId())
                .member(member)
                .build();

        boardRepository.save(board);

        Integer InsertResult = board.getId();

        List<Attach>filelist = fileHandler.parseFileInfo(requestDto.getFiles());

        if(filelist == null || filelist.size() == 0){
            return InsertResult;
        }
        if(!filelist.isEmpty()){
            for(Attach attachFile : filelist){
                board.addAttach(attachRepository.save(attachFile));
            }
        }
        return InsertResult;
    }

    /**
    *  게시글 수정(파일 첨부)
    **/
    @Transactional
    public Integer BoardUpdate(Integer boardId, BoardDto.BoardRequestDto dto, Member member,List<MultipartFile>files) throws Exception {

        if(member==null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<Board>detail = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        String boardAuthor = detail.get().getBoardAuthor();
        String userId = member.getUserId();

        if(!boardAuthor.equals(userId)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        detail.get().boardUpdate(dto);

        int UpdateResult = detail.get().getId();

        List<Attach>filelist = attachRepository.findAttachBoard(boardId);

        //파일이 있는 경우에 수정하는 경우
        if(!filelist.isEmpty()){
            for(int i =0; i<filelist.size();i++){
                String filePath = filelist.get(i).getFilePath();
                File file = new File(filePath);

                if(file.exists()){
                    file.delete();
                }
                attachService.deleteBoardAttach(boardId);
            }
            filelist = fileHandler.parseFileInfo(files);

            for(Attach attachFile : filelist){
                detail.get().addAttach(attachRepository.save(attachFile));
            }
        }else{
            filelist = fileHandler.parseFileInfo(files);

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

        Board  board = detail.get();

        String boardAuthor = board.getBoardAuthor();
        String userId = member.getUserId();

        if(!boardAuthor.equals(userId)){
            throw new CustomExceptionHandler(ERRORCODE.NOT_AUTH);
        }

        List<AttachDto>attachlist = attachService.boardfilelist(boardId);

        for(int i = 0; i<attachlist.size();i++){

            String filePath = attachlist.get(i).getFilePath();
            File file = new File(filePath);

            if(file.exists()){
                file.delete();
            }
        }

        boardRepository.deleteById(boardId);
    }

    /**
    * 게시글 비밀번호 확인
    **/
    @Transactional
    public BoardDto.BoardResponseDto passwordCheck(String passWd,Integer id,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<BoardDto.BoardResponseDto> result = Optional
                .of(Optional
                        .ofNullable(
                                boardRepository.findByPassWdAndId(passWd, id))
                                    .orElseThrow(()-> new CustomExceptionHandler(ERRORCODE.NOT_MATCH_PASSWORD)));

        return result.get();
    }
    
    /**
    * 게시글 조회수 증가
    **/
    @Transactional
    public Integer updateView(Integer boardId){
        return boardRepository.ReadCountUp(boardId);
    }
}

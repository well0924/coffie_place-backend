package com.example.coffies_vol_02.Board.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Config.Redis.CacheKey;
import com.example.coffies_vol_02.Config.Redis.RedisService;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@AllArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;

    private final RedisService redisService;

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
    public Page<BoardDto.BoardResponseDto> boardSearchAll(String searchVal, Pageable pageable){
        return boardRepository.findAllSearch(searchVal,pageable);
    }

    /**
    *  게시글 단일 조회
    **/
    @Transactional(readOnly = true)
    public BoardDto.BoardResponseDto boardDetail(Integer boardId){
        Optional<Board> boardDetail= Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        Board result = boardDetail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST));

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
    public BoardDto.BoardResponseDto passwordCheck(String passWd,Integer id,Member member){

        if(member == null){
            throw new CustomExceptionHandler(ERRORCODE.ONLY_USER);
        }

        Optional<BoardDto.BoardResponseDto> boardDetail = Optional.of(Optional.ofNullable(boardRepository.findByPassWdAndId(passWd, id)).orElseThrow(()-> new CustomExceptionHandler(ERRORCODE.NOT_MATCH_PASSWORD)));

        return boardDetail.orElse(null);
    }

    //게시글 조회수
    public void boardViewCount(Integer boardId){
        String countKey = CacheKey.BOARD+"ViewCount"+"::"+boardId;

        if(redisService.getData(countKey)==null){
            //조회가 처음이면 redis의 값을 저장한다.
            redisService.setValues(countKey,String.valueOf(boardRepository.ReadCount(boardId)+1), Duration.ofMinutes(CacheKey.BOARD_EXPIRE_SEC));
        }else if(redisService.getData(countKey)!=null){
            //값이 있으면 조회수를 증가.
            redisService.increasement(countKey);
        }
    }

    //게시글 조회수 반영 10초마다 실행.
    @Scheduled(cron = "0/10 * * * * ?",zone = "Asia/Seoul")
    public void boardViewCountDB(){
        Set<String> viewKeys = redisService.keys("boardViewCount*");

        log.info("boardViewCount::"+viewKeys);

        if(Objects.requireNonNull(viewKeys).isEmpty())return;

        for(String viewKey : viewKeys){

            Integer boardId = Integer.parseInt(viewKey.split("::")[1]);
            Integer viewCount = Integer.parseInt(redisService.getData(viewKey));

            log.info("게시글 번호:"+boardId);
            log.info("조회수:"+viewCount);

            boardRepository.ReadCountUpToDB(boardId,viewCount);

            redisService.deleteValues(viewKey);
            redisService.deleteValues(CacheKey.BOARD+"ViewCount"+"::"+boardId);
        }
    }
}

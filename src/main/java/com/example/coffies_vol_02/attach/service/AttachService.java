package com.example.coffies_vol_02.attach.service;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@AllArgsConstructor
public class AttachService {
    private final AttachRepository attachRepository;

    /**
     * 파일 전체 목록(자유게시판)
     * @author 양경빈
     * @param boardId 게시글 번호
     * @return list 자유게시글 첨부파일 목록
     * @see AttachRepository#findAttachBoard(Integer) 자유게시판 첨부파일 목록조회
     **/
    @Transactional(readOnly = true)
    public List<AttachDto> boardfilelist(@Param("id") Integer boardId)throws Exception{
        List<Attach>list = attachRepository.findAttachBoard(boardId);
        return getFreeBoardAttach(list);
    }

    /**
     * 파일 전체 목록(공지 게시판)
     * @author 양경빈
     * @param noticeId 공지게시글 번호
     * @return noticeList 공지게시글 첨부파일 목록
     * @see AttachRepository#findAttachNoticeBoard(Integer) 공지게시글 첨부파일 목록을 조회하는 메서드
     **/
    @Transactional(readOnly = true)
    public List<AttachDto>noticefilelist(@Param("id")Integer noticeId)throws Exception{
        List<Attach>noticeList = attachRepository.findAttachNoticeBoard(noticeId);
        return getNoticeBoardAttach(noticeList);
    }

    /**
     * 자유 게시판 파일 삭제
     * @author 양경빈
     * @param Id 게시글 번호
     * @see AttachRepository#findAttachBoard(Integer) 자유게시판에서 첨부파일목록을 조회하는 메서드
     * @see AttachRepository#delete(Object) 첨부파일을 삭제하는 메서드
     **/
    public void deleteBoardAttach(Integer Id) throws Exception {
        List<Attach>list = attachRepository.findAttachBoard(Id);

        for (Attach attach : list) {
            attachRepository.delete(attach);
        }
        log.info("file service");
        log.info("filelist:"+list);
    }

    /**
     * 공지 게시판 파일 삭제
     * @author 양경빈
     * @param Id 공지게시판 번호
     **/
    public void deleteNoticeAttach(Integer Id) throws Exception {
        List<Attach>list = attachRepository.findAttachNoticeBoard(Id);

        for (Attach attach : list) {
            attachRepository.delete(attach);
        }
        log.info("file service");
        log.info("filelist:"+list);
    }

    /**
     * 자유게시판 파일 조회
     * @author 양경빈
     * @param fileName 원본 파일명
     * @exception CustomExceptionHandler 첨부파일이 없는경우 NOT_FILE
     * @return AttachDto
     * @see AttachRepository#findAttachByOriginFileName(String) 원본파일명으로 파일을 조회 파일이 없는 경우에는 NOT_FILE
     **/
    @Transactional(readOnly = true)
    public AttachDto getFreeBoardFile(String fileName){
        Optional<Attach> detail = Optional.ofNullable(attachRepository
                .findAttachByOriginFileName(fileName).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FILE)));

        Attach getFile = detail.get();

        AttachDto result = AttachDto
                .builder()
                .originFileName(getFile.getOriginFileName())
                .fileSize(getFile.getFileSize())
                .filePath(getFile.getFilePath())
                .boardId(getFile.getBoard().getId())
                .build();

        return result;
    }

    /**
     * 공지게시판 파일 조회
     * @author 양경빈
     * @param fileName 원본 파일명
     * @exception CustomExceptionHandler 첨부파일이 없는경우 NOT_FILE
     * @return AttachDto
     * @see AttachRepository#findAttachByOriginFileName(String) 원본파일명으로 파일을 조회 파일이 없는 경우에는 NOT_FILE
     **/
    @Transactional(readOnly = true)
    public AttachDto getNoticeBoardFile(String fileName){
        Optional<Attach> result = Optional.ofNullable(attachRepository
                .findAttachByOriginFileName(fileName).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FILE)));

        Attach detail = result.get();

        AttachDto getFile = AttachDto
                .builder()
                .originFileName(detail.getOriginFileName())
                .fileSize(detail.getFileSize())
                .filePath(detail.getFilePath())
                .noticeId(detail.getNoticeBoard().getId())
                .build();

        return getFile;
    }

    /**
     * 자유게시판 파일목록을 추출하는 메서드
     * @author 양경빈
     * @param list 첨부파일들
     * @return filelist 자유게시판 첨부파일 목록들
     **/
    private List<AttachDto> getFreeBoardAttach(List<Attach> list) {
        List<AttachDto>filelist = new ArrayList<>();

        for(Attach file : list){

            AttachDto attachDto = AttachDto
                    .builder()
                    .filePath(file.getFilePath())
                    .originFileName(file.getOriginFileName())
                    .fileSize(file.getFileSize())
                    .boardId(file.getBoard().getId())
                    .build();

            log.info(attachDto);
            filelist.add(attachDto);
        }
        return filelist;
    }

    /**
     * 공지게시판 파일목록을 추출하는 메서드
     * @author 양경빈
     * @param list 첨부파일들
     * @return filelist 첨부파일리스트
     **/
    private List<AttachDto> getNoticeBoardAttach(List<Attach> list) {
        List<AttachDto>filelist = new ArrayList<>();

        for(Attach file : list){

            AttachDto attachDto = AttachDto
                    .builder()
                    .filePath(file.getFilePath())
                    .originFileName(file.getOriginFileName())
                    .fileSize(file.getFileSize())
                    .noticeId(file.getNoticeBoard().getId())
                    .build();

            log.info(attachDto);

            filelist.add(attachDto);
        }
        return filelist;
    }

}

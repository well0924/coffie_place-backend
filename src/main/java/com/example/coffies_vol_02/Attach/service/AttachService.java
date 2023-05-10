package com.example.coffies_vol_02.Attach.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Log4j2
@Service
@AllArgsConstructor
public class AttachService {
    private final AttachRepository attachRepository;

    /**
    *  파일 전체 목록(자유게시판)
    **/
    @Transactional(readOnly = true)
    public List<AttachDto> boardfilelist(@Param("id") Integer boardId)throws Exception{
        List<Attach>list = attachRepository.findAttachBoard(boardId);
        return getFreeBoardAttach(list);
    }

    /**
    *  파일 전체 목록(공지 게시판)
     **/
    @Transactional
    public List<AttachDto>noticefilelist(@Param("id")Integer noticeId)throws Exception{
        List<Attach>noticeList = attachRepository.findAttachNoticeBoard(noticeId);
        return getNoticeBoardAttach(noticeList);
    }

    /**
    * 자유 게시판 파일 삭제
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
    * 파일 조회
    **/
    @Transactional(readOnly = true)
    public AttachDto getFreeBoardFile(String fileName){
        Optional<Attach> detail = Optional.ofNullable(attachRepository.findAttachByOriginFileName(fileName).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FILE)));

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

    @Transactional(readOnly = true)
    public AttachDto getNoticeBoardFile(String fileName){
        Optional<Attach> result = Optional.ofNullable(attachRepository.findAttachByOriginFileName(fileName).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.NOT_FILE)));
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

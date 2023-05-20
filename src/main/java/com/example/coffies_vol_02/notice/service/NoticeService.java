package com.example.coffies_vol_02.notice.service;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequestDto;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponseDto;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NoticeService {
    private final NoticeBoardRepository noticeBoardRepository;
    private final AttachRepository attachRepository;
    private final AttachService attachService;
    private final FileHandler fileHandler;

    /**
    *   공지글 목록
    **/
    @Transactional(readOnly = true)
    public Page<NoticeResponseDto>noticeAllList(Pageable pageable){
        return noticeBoardRepository.findAllList(pageable);
    }

    /**
    *   공지 게시물 검색
    **/
    @Transactional(readOnly = true)
    public Page<NoticeResponseDto>noticeSearchAll(String searchVal,Pageable pageable){
        return noticeBoardRepository.findAllSearchList(searchVal,pageable);
    }

    /**
    *   공지글 단일 조회
    **/
    @Transactional(readOnly = true)
    public NoticeResponseDto findNotice(Integer noticeId){
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        NoticeBoard noticeBoard = detail.orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

        return NoticeResponseDto
                .builder()
                .id(noticeBoard.getId())
                .noticeTitle(noticeBoard.getNoticeTitle())
                .noticeWriter(noticeBoard.getNoticeWriter())
                .noticeContents(noticeBoard.getNoticeContents())
                .noticeGroup(noticeBoard.getNoticeGroup())
                .fileGroupId(noticeBoard.getFileGroupId())
                .isFixed(noticeBoard.getIsFixed())
                .createdTime(noticeBoard.getCreatedTime())
                .updatedTime(noticeBoard.getUpdatedTime())
                .build();
    }

    /**
    *   공지글 작성
    **/
    @Transactional
    public Integer noticeCreate(NoticeRequestDto dto, List<MultipartFile>files) throws Exception {

        NoticeBoard noticeBoard = NoticeBoard
                .builder()
                .noticeContents(dto.getNoticeContents())
                .noticeGroup(dto.getNoticeGroup())
                .noticeTitle(dto.getNoticeTitle())
                .noticeWriter(dto.getNoticeWriter())
                .isFixed(dto.getIsFixed())
                .fileGroupId(dto.getFileGroupId())
                .build();

        noticeBoardRepository.save(noticeBoard);

        Integer noticeInsertResult = noticeBoard.getId();

        List<Attach>filelist = fileHandler.parseFileInfo(files);

        if(filelist == null || filelist.size() == 0){
            return noticeInsertResult;
        }
        if(!filelist.isEmpty()){
            for(Attach attachFile : filelist){
                noticeBoard.addAttach(attachRepository.save(attachFile));
            }
        }
        return noticeInsertResult;
    }
    
    /**
    * 공지 게시글 수정
    **/
    @Transactional
    public Integer noticeUpdate(Integer noticeId,NoticeRequestDto dto,List<MultipartFile>files) throws Exception {
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        NoticeBoard noticeBoard = detail.orElse(null);

        noticeBoard.NoticeUpdate(dto);

        int updateResult = noticeBoard.getId();

        List<Attach>filelist = fileHandler.parseFileInfo(files);

        if(!filelist.isEmpty()){

            for (Attach attach : filelist) {
                String filePath = attach.getFilePath();

                File file = new File(filePath);

                if (file.exists()) {
                    file.delete();
                }
                attachService.deleteNoticeAttach(noticeId);
            }
            for(Attach attachFile : filelist){
                detail.get().addAttach(attachRepository.save(attachFile));
            }
        }
        return updateResult;
    }

    /*
    * 공지 게시글 삭제
    */
    @Transactional
    public void noticeDelete(Integer noticeId)throws Exception{
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        List<Attach>list = attachRepository.findAttachNoticeBoard(noticeId);

        for (Attach attach : list) {
            String filePath = attach.getFilePath();
            File file = new File(filePath);

            if (file.exists()) {
                file.delete();
            }
        }
        noticeBoardRepository.deleteById(noticeId);
    }
}

package com.example.coffies_vol_02.notice.service;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.repository.AttachRepository;
import com.example.coffies_vol_02.attach.service.AttachService;
import com.example.coffies_vol_02.config.redis.CacheKey;
import com.example.coffies_vol_02.config.util.FileHandler;
import com.example.coffies_vol_02.config.exception.ERRORCODE;
import com.example.coffies_vol_02.config.exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.notice.domain.NoticeBoard;
import com.example.coffies_vol_02.notice.domain.dto.request.NoticeRequest;
import com.example.coffies_vol_02.notice.domain.dto.response.NoticeResponse;
import com.example.coffies_vol_02.notice.repository.NoticeBoardRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
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

    @Transactional(readOnly = true)
    public Page<NoticeResponse>noticeAllList(Pageable pageable){
        return noticeBoardRepository.findAllList(pageable);
    }

    @Transactional(readOnly = true)
    public Page<NoticeResponse>noticeSearchAll(String searchVal,Pageable pageable){
        return noticeBoardRepository.findAllSearchList(searchVal,pageable);
    }

    @Cacheable(value = CacheKey.NOTICE_BOARD, key = "#noticeId")
    @Transactional(readOnly = true)
    public NoticeResponse findNotice(Integer noticeId){
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository
                .findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        NoticeBoard noticeBoard = detail
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

        NoticeResponse response = new NoticeResponse(noticeBoard);

        return response;
    }

    @Transactional
    public Integer noticeCreate(NoticeRequest dto, List<MultipartFile>files) throws Exception {
        NoticeBoard noticeBoard = new NoticeBoard();

        noticeBoardRepository.save(dto.toEntity(noticeBoard));

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
    
    @Transactional
    public Integer noticeUpdate(Integer noticeId,NoticeRequest dto,List<MultipartFile>files) throws Exception {

        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository
                .findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        NoticeBoard noticeBoard = detail
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));

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

    @Transactional
    public void noticeDelete(Integer noticeId)throws Exception{

        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository
                .findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

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

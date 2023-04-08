package com.example.coffies_vol_02.Notice.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import com.example.coffies_vol_02.Attach.service.AttachService;
import com.example.coffies_vol_02.Attach.service.FileHandler;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import com.example.coffies_vol_02.Notice.domain.NoticeBoard;
import com.example.coffies_vol_02.Notice.domain.dto.NoticeBoardDto;
import com.example.coffies_vol_02.Notice.repository.NoticeBoardRepository;
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

    /*
    * 공지글 목록
    */
    @Transactional(readOnly = true)
    public Page<NoticeBoardDto.BoardResponseDto>noticeList(Pageable pageable){
        Page<NoticeBoard>noticeBoards = noticeBoardRepository.findAll(pageable);

        return noticeBoards.map(noticeBoard -> new NoticeBoardDto.BoardResponseDto(noticeBoard));
    }
    
    /*
    *   공지글 단일 조회
    */
    @Transactional(readOnly = true)
    public NoticeBoardDto.BoardResponseDto noticeDetail(Integer noticeId){
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        NoticeBoard noticeBoard = detail.get();

        return NoticeBoardDto.BoardResponseDto
                .builder()
                .noticeBoard(noticeBoard)
                .build();
    }
    /*
    *  공지글 작성
    */
    @Transactional
    public Integer noticeWrite(NoticeBoardDto.BoardRequestDto dto,List<MultipartFile>files) throws Exception {

        NoticeBoard noticeBoard = NoticeBoard
                .builder()
                .noticeContents(dto.getNoticeContents())
                .noticeGroup(dto.getNoticeGroup())
                .noticeTitle(dto.getNoticeTitle())
                .noticeWriter(dto.getNoticeWriter())
                .isFixed(dto.getIsFixed())
                .fileGroupId(dto.getFileGroupId())
                .build();

        int noticeInsertResult = noticeBoardRepository.save(noticeBoard).getId();

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
    
    /*
    * 공지 게시글 수정
    */
    @Transactional
    public Integer noticeUpdate(Integer noticeId,NoticeBoardDto.BoardRequestDto dto,List<MultipartFile>files) throws Exception {
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        NoticeBoard noticeBoard = detail.get();

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

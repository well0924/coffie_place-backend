package com.example.coffies_vol_02.Notice.service;

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
import org.springframework.web.multipart.MultipartRequest;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class NoticeService {
    private final NoticeBoardRepository noticeBoardRepository;
    private final FileHandler fileHandler;

    /*
    * 공지글 목록
    */
    @Transactional(readOnly = true)
    public Page<NoticeBoardDto.BoardResponseDto>noticeList(Pageable pageable){
        Page<NoticeBoard>noticeBoards = noticeBoardRepository.findAll(pageable);

        if(noticeBoards.isEmpty()){
            throw new CustomExceptionHandler(ERRORCODE.BOARD_NOT_LIST);
        }
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
    @Transactional
    public Integer noticeWrite(NoticeBoardDto.BoardRequestDto dto){
        NoticeBoard noticeBoard = NoticeBoard
                .builder()
                .noticeContents(dto.getNoticeContents())
                .noticeGroup(dto.getNoticeGroup())
                .noticeTitle(dto.getNoticeTitle())
                .noticeWriter(dto.getNoticeWriter())
                .isFixed(dto.getIsFixed())
                .fileGroupId(dto.getFileGroupId())
                .build();
        int InsertResult = noticeBoardRepository.save(noticeBoard).getId();

        return InsertResult;
    }
    @Transactional
    public Integer noticeUpdate(Integer noticeId,NoticeBoardDto.BoardRequestDto dto){
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        NoticeBoard noticeBoard = detail.get();

        noticeBoard.NoticeUpdate(dto);

        int updateResult = noticeBoard.getId();

        return updateResult;
    }
    @Transactional
    public void noticeDelete(Integer noticeId){
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository.findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        NoticeBoard noticeBoard = detail.get();
        noticeBoardRepository.deleteById(noticeId);
    }
}

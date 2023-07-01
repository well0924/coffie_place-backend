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

    /**
     * 공지글 목록
     * @author 양경빈
     * @param pageable 페이징 객체
     * @return Page<NoticeResponse>
     * @see NoticeBoardRepository#findAllList(Pageable) 공지게시판 목록 조회 메서드
     **/
    @Transactional(readOnly = true)
    public Page<NoticeResponse>noticeAllList(Pageable pageable){
        return noticeBoardRepository.findAllList(pageable);
    }

    /**
     * 공지 게시물 검색
     * @author 양경빈
     * @param pageable 페이징 객체
     * @param searchVal 검색어
     * @return Page<NoticeResponse>
     * @see NoticeBoardRepository#findAllSearchList(String, Pageable) 공지게시판에서 검색을 하는 메서드
     **/
    @Transactional(readOnly = true)
    public Page<NoticeResponse>noticeSearchAll(String searchVal,Pageable pageable){
        return noticeBoardRepository.findAllSearchList(searchVal,pageable);
    }

    /**
     * 공지글 단일 조회(redis 캐시 적용)
     * @author 양경빈
     * @param noticeId 공지게시판 번호 번호가 없는 경우에는 BOARD_NOT_FOUND 발생
     * @return NoticeResponse
     * @see NoticeBoardRepository#findById(Object) 공지제시글 번호를 조회해서 게시글 조회 조회하는 번호가 없는 경우에는 BOARD_NOT_FOUND
     **/
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

    /**
     * 공지글 작성
     * @author 양경빈
     * @param dto 게시물에 필요한 dto(공지게시물 종류와 내용을 작성하지 않은 경우에는 유효성 검사)
     * @param files 공지 게시글에 필요한 첨부파일
     * @see FileHandler#parseFileInfo(List) 게시물 작성 및 수정시 파일 첨부하는 메서드
     * @see NoticeBoardRepository#save(Object) 게시물 저장
     * @see AttachRepository#save(Object) 첨부파일 저장
     * @return noticeInsertResult 생성된 게시물 번호
     **/
    @Transactional
    public Integer noticeCreate(NoticeRequest dto, List<MultipartFile>files) throws Exception {

        NoticeBoard noticeBoard = NoticeBoard
                .builder()
                .noticeTitle(dto.noticeTitle())
                .noticeGroup(dto.noticeGroup())
                .noticeContents(dto.noticeContents())
                .noticeWriter(dto.noticeWriter())
                .isFixed(dto.isFixed())
                .fileGroupId(dto.fileGroupId())
                .build();

        noticeBoardRepository.save(noticeBoard);

        Integer noticeInsertResult = noticeBoard.getId();

        //파일 업로드
        List<Attach>filelist = fileHandler.parseFileInfo(files);

        //파일이 없는 경우 단순 글작성
        if(filelist == null || filelist.size() == 0){
            return noticeInsertResult;
        }

        //첨부 파일이 있는 경우 첨부파일 저장
        if(!filelist.isEmpty()){
            for(Attach attachFile : filelist){
                noticeBoard.addAttach(attachRepository.save(attachFile));
            }
        }

        return noticeInsertResult;
    }

    /**
     * 공지 게시글 수정
     * @author 양경빈
     * @param noticeId 공지게시물 번호 게시물 번호가 없는 경우에는 발생 BOARD_NOT_FOUND 발생
     * @param dto 게시물 수정에 필요한  dto
     * @param files 게시물 수정시 파일 첨부에 필요한 매개변수
     * @exception CustomExceptionHandler 조회하는 게시물이 없는 경우
     * @see NoticeBoardRepository#findById(Object) 게시물 단일 조회에 사용되는 메서드
     * @see FileHandler#parseFileInfo(List) 게시물 작성 및 수정을 했을 경우 파일을 업로드 하는 메서드
     * @see AttachRepository#findAttachBoard(Integer)  공지게시글에 있는 첨부파일 목록을 조회하는 메서드
     * @see AttachService#deleteNoticeAttach(Integer) 공지게시글에 있는 첨부파일을 삭제하는 메서드
     * @see AttachRepository#save(Object) 첨부파일을 저장하는 메서드
     * @return UpdateResult 수정된 게시글 번호
     **/
    @Transactional
    public Integer noticeUpdate(Integer noticeId,NoticeRequest dto,List<MultipartFile>files) throws Exception {
        //공지 게시글 조회
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository
                .findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        NoticeBoard noticeBoard = detail
                .orElseThrow(()->new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND));
        //공지 게시글 수정
        noticeBoard.NoticeUpdate(dto);
        
        int updateResult = noticeBoard.getId();

        List<Attach>filelist = fileHandler.parseFileInfo(files);
        //첨부파일이 있는 경우
        if(!filelist.isEmpty()){

            for (Attach attach : filelist) {
                String filePath = attach.getFilePath();

                File file = new File(filePath);
                //저장된 파일을 삭제
                if (file.exists()) {
                    file.delete();
                }
                //디비에 저장된 파일을 삭제
                attachService.deleteNoticeAttach(noticeId);
            }
            for(Attach attachFile : filelist){
                detail.get().addAttach(attachRepository.save(attachFile));
            }
        }
        return updateResult;
    }

    /**
     * 공지 게시글 삭제
     * @author 양경빈
     * @param noticeId 공지게시물 번호 게시물 번호가 없는 경우에는 발생 BOARD_NOT_FOUND 발생
     * @exception CustomExceptionHandler 조회하는 게시물이 없는 경우
     * @see NoticeBoardRepository#findById(Object) 게시물 단일 조회에 사용되는 메서드
     * @see AttachRepository#findAttachNoticeBoard(Integer)  공지게시글에 있는 첨부파일 목록을 조회하는 메서드
     * @see NoticeBoardRepository#deleteById(Object) 게시물 번호로 게시물을 삭제
     **/
    @Transactional
    public void noticeDelete(Integer noticeId)throws Exception{
        //공지 게시글 조회
        Optional<NoticeBoard>detail = Optional.ofNullable(noticeBoardRepository
                .findById(noticeId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));
        //첨부파일 조회
        List<Attach>list = attachRepository.findAttachNoticeBoard(noticeId);

        for (Attach attach : list) {
            String filePath = attach.getFilePath();

            File file = new File(filePath);
            //첨부 파일 삭제
            if (file.exists()) {
                file.delete();
            }
        }
        noticeBoardRepository.deleteById(noticeId);
    }
}

package com.example.coffies_vol_02.Attach.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class AttachService {
    private final AttachRepository attachRepository;

    /*
    *  파일 전체 목록(자유게시판)
    */
    @Transactional(readOnly = true)
    public List<AttachDto> boardfilelist(@Param("id") Integer boardId)throws Exception{
        List<Attach>list = attachRepository.findAttachBoard(boardId);
        return getAttach(list);
    }

    /*
    *  파일 전체 목록(공지 게시판)
     */
    @Transactional
    public List<AttachDto>noticefilelist(@Param("id")Integer noticeId)throws Exception{
        List<Attach>noticeList = attachRepository.findAttachNoticeBoard(noticeId);

        return getAttach(noticeList);
    }

    private List<AttachDto> getAttach(List<Attach> list) {
        List<AttachDto>filelist = new ArrayList<>();

        for(Attach file : list){

            AttachDto attachDto = AttachDto
                    .builder()
                    .filePath(file.getFilePath())
                    .originFileName(file.getOriginFileName())
                    .fileSize(file.getFileSize())
                    .boardId(file.getId())
                    .build();

            filelist.add(attachDto);
        }
        return filelist;
    }

}

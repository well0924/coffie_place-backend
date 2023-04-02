package com.example.coffies_vol_02.Attach.service;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Attach.domain.AttachDto;
import com.example.coffies_vol_02.Attach.repository.AttachRepository;
import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Board.repository.BoardRepository;
import com.example.coffies_vol_02.Config.Exception.ERRORCODE;
import com.example.coffies_vol_02.Config.Exception.Handler.CustomExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttachService {
    private final AttachRepository attachRepository;
    private final BoardRepository boardRepository;

    /*
    *  파일 전체 목록
    */
    @Transactional(readOnly = true)
    public List<AttachDto> filelist(@Param("id") Integer boardId)throws Exception{
        Optional<Board> findBoard = Optional.ofNullable(boardRepository.findById(boardId).orElseThrow(() -> new CustomExceptionHandler(ERRORCODE.BOARD_NOT_FOUND)));

        List<Attach>list = attachRepository.findAttachBoard(boardId);

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

package com.example.coffies_vol_02.Factory;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.attach.domain.AttachDto;

public class FileFactory {

    public static Attach attach(){
        return Attach
                .builder()
                .originFileName("c.jpg")
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .fileSize(30277L)
                .noticeBoard(NoticeFactory.noticeBoard())
                .board(BoardFactory.board())
                .build();
    }
    public static AttachDto attachDto(){
        return AttachDto
                .builder()
                .boardId(BoardFactory.board().getId())
                .noticeId(1)
                .originFileName("c.jpg")
                .fileSize(30277L)
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .build();
    }

    public static AttachDto noticeDto(){
        return AttachDto.builder()
                .noticeId(NoticeFactory.noticeBoard().getId())
                .originFileName("c.jpg")
                .fileSize(30277L)
                .filePath("C:\\\\UploadFile\\\\\\1134003220710700..jpg")
                .build();
    }
}

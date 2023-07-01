package com.example.coffies_vol_02.attach.domain;

import lombok.*;

@Getter
@Setter
@ToString
public class AttachDto {
    private String originFileName;
    private String filePath;
    private Long fileSize;
    private Integer boardId;
    private Integer noticeId;

    @Builder
    public AttachDto(String originFileName,String filePath,Long fileSize,Integer boardId,Integer noticeId){
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        if(boardId != null){
            this.boardId = boardId;
        }
        if(noticeId!=null){
            this.noticeId = noticeId;
        }
    }
}

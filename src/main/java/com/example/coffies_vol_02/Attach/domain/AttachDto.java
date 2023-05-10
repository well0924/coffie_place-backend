package com.example.coffies_vol_02.Attach.domain;

import lombok.*;

@Getter
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
        this.boardId = boardId;
        this.noticeId = noticeId;
    }
}

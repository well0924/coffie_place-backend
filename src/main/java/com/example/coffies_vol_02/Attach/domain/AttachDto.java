package com.example.coffies_vol_02.Attach.domain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
public class AttachDto {
    private String originFileName;
    private String filePath;
    private Long fileSize;
    private Integer boardId;

    @Builder
    public AttachDto(String originFileName,String filePath,Long fileSize,Integer boardId){
        this.originFileName = originFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.boardId = boardId;
    }
}

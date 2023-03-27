package com.example.coffies_vol_02.Board.domain.dto;

import lombok.*;

import java.time.LocalDateTime;

public class BoardDto {
    @Setter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRequestDto{
        private String boardTitle;
        private String boardContents;
        private String boardAuthor;
        private Integer readCount;
        private Integer passWd;
        private String fileGroupId;
    }
    @Getter
    @Builder
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardResponseDto{
        private Integer id;
        private String boardTitle;
        private String boardContents;
        private String boardAuthor;
        private Integer readCount;
        private Integer passWd;
        private String fileGroupId;
        private LocalDateTime createdTime;
        private LocalDateTime updatedTime;
    }
}

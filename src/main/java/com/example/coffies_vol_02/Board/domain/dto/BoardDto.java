package com.example.coffies_vol_02.Board.domain.dto;

import com.example.coffies_vol_02.Board.domain.Board;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

public class BoardDto {
    @Setter
    @Getter
    @ToString
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoardRequestDto{
        @NotBlank(message = "제목을 작성해 주세요.")
        private String boardTitle;
        @NotBlank(message = "내용을 입력해 주세요.")
        private String boardContents;
        private String boardAuthor;
        private Integer readCount;
        private String passWd;
        private String fileGroupId;
        private List<MultipartFile> files;
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
        private String passWd;
        private Integer liked;
        private String fileGroupId;
        private LocalDateTime createdTime;
        private LocalDateTime updatedTime;
        @Builder
        public BoardResponseDto(Board board){
            this.id = board.getId();
            this.boardTitle = board.getBoardTitle();
            this.boardAuthor = board.getMember().getUserId();
            this.boardContents = board.getBoardContents();
            this.readCount = board.getReadCount();
            this.passWd = board.getPassWd();
            this.fileGroupId = board.getFileGroupId();
            this.createdTime = board.getCreatedTime();
            this.updatedTime = board.getUpdatedTime();
        }
    }
}

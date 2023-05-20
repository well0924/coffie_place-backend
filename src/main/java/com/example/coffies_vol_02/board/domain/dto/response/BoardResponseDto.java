package com.example.coffies_vol_02.board.domain.dto.response;

import com.example.coffies_vol_02.board.domain.Board;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Schema(description = "게시물 리스트 응답DTO")
@Setter
@Getter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponseDto implements Serializable {
    @Schema(description = "게시글 번호")
    private Integer id;
    @Schema(description = "게시물 제목")
    private String boardTitle;
    @Schema(description = "게시물 내용")
    private String boardContents;
    @Schema(description = "게시물 작성자")
    private String boardAuthor;
    @Schema(description = "게시물 조회수")
    private Integer readCount;
    @Schema(description = "게시물 비밀번호")
    private String passWd;
    @Schema(description = "게시물 좋아요수")
    private Integer liked;
    @Schema(description = "게시물 파일 그룹 아이디")
    private String fileGroupId;
    @Schema(description = "게시물 작성일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime createdTime;
    @Schema(description = "게시물 수정일")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime updatedTime;

    @Builder
    @QueryProjection
    public BoardResponseDto(Board board){
        this.id = board.getId();
        this.boardTitle = board.getBoardTitle();
        this.boardAuthor = board.getMember().getUserId();
        this.boardContents = board.getBoardContents();
        this.readCount = board.getReadCount();
        this.passWd = board.getPassWd();
        this.liked = board.getLikes().size();
        this.fileGroupId = board.getFileGroupId();
        this.createdTime = board.getCreatedTime();
        this.updatedTime = board.getUpdatedTime();
    }
}

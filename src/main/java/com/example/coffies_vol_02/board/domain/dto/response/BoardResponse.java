package com.example.coffies_vol_02.board.domain.dto.response;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.commnet.domain.dto.response.CommentResponse;
import com.example.coffies_vol_02.like.domain.Like;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Schema(description = "게시판 응답Dto")
public record BoardResponse(
        @Schema(description = "게시글 번호")
        Integer id,
        @Schema(description = "게시물 제목")
        String boardTitle,
        @Schema(description = "게시물 작성자")
        String boardAuthor,
        @Schema(description = "게시물 내용")
        String boardContents,
        @Schema(description = "게시물 조회수")
        Integer readCount,
        @Schema(description = "게시물 조회수")
        Integer liked,
        @Schema(description = "게시물 비밀번호")
        String passWd,
        @Schema(description = "게시물 파일 그룹 아이디")
        String fileGroupId,
        @Schema(description = "댓글 목록")
        List<CommentResponse> commentList,
        @Schema(description = "게시물 작성일")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime createdTime,
        @Schema(description = "게시물 수정일")
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
        LocalDateTime updatedTime

)implements Serializable {

        @QueryProjection
        public BoardResponse(Board board){
                this(
                        board.getId(),
                        board.getBoardTitle(),
                        board.getMember().getUserId(),
                        board.getBoardContents(),
                        board.getReadCount(),
                        board.getLiked().size(),
                        board.getPassWd(),
                        board.getFileGroupId(),
                        board.getCommentList()
                                .stream()
                                .map(comment->new CommentResponse(comment))
                                .collect(Collectors.toList()),
                        board.getCreatedTime(),
                        board.getUpdatedTime()
                );
        }
}

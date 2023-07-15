package com.example.coffies_vol_02.board.domain.dto.response;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.commnet.domain.dto.response.placeCommentResponseDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.querydsl.core.annotations.QueryProjection;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel(value = "게시글 응답 record",description = "게시판 응답Dto")
public record BoardResponse(
        @Schema(name = "id",description = "게시글 번호",type = "Integer")
        Integer id,
        @Schema(name = "boardTitle",description = "게시물 제목",type = "String")
        String boardTitle,
        @Schema(name = "boardAuthor",description = "게시물 작성자",type = "String")
        String boardAuthor,
        @Schema(name = "boardContents",description = "게시물 내용",type = "String")
        String boardContents,
        @Schema(name = "readCount",description = "게시물 조회수",type = "Integer")
        Integer readCount,
        @Schema(name = "liked",description = "게시물 좋아요",type = "Integer")
        Integer liked,
        @Schema(name = "passWd",description = "게시물 비밀번호",type = "String")
        String passWd,
        @Schema(name = "fileGroupId",description = "게시물 파일 그룹 아이디",type = "String")
        String fileGroupId,
        @Schema(name = "commentList",description = "댓글 목록",type = "List")
        List<placeCommentResponseDto> commentList,
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
                                .map(comment->new placeCommentResponseDto(comment))
                                .collect(Collectors.toList()),
                        board.getCreatedTime(),
                        board.getUpdatedTime()
                );
        }
}

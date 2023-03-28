package com.example.coffies_vol_02.Board.domain;

import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;

@Entity
@Getter
@ToString
@NoArgsConstructor
public class Board extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String boardTitle;
    private String boardContents;
    private String boardAuthor;
    private Integer readCount;
    private Integer passWd;
    private String fileGroupId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @ToString.Exclude
    private Member member;

    @Builder
    public Board(Integer id, String boardContents, String boardTitle, Integer readCount, Integer passWd, String fileGroupId, Member member){
        this.id = id;
        this.boardTitle = boardTitle;
        this.boardAuthor = member.getUserId();
        this.boardContents = boardContents;
        this.readCount = readCount;
        this.passWd = passWd;
        this.fileGroupId = fileGroupId;
        this.member = member;
    }

    public void countUp() {
        this.readCount ++;
    }

    public void boardUpdate(BoardDto.BoardRequestDto dto){
        this.boardTitle = dto.getBoardTitle();
        this.boardContents = dto.getBoardContents();
    }

}

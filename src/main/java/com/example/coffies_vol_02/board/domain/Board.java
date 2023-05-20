package com.example.coffies_vol_02.board.domain;

import com.example.coffies_vol_02.attach.domain.Attach;
import com.example.coffies_vol_02.board.domain.dto.request.BoardRequestDto;
import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.like.domain.Like;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.*;

@Entity
@Getter
@Proxy(lazy = false)
@NoArgsConstructor
@Table(name = "tbl_board",
        indexes = {
                @Index(name = "board_index1",columnList = "boardTitle"),
                @Index(name = "board_index2",columnList = "boardAuthor"),
                @Index(name = "board_index3",columnList = "id")})
public class Board extends BaseTime implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String boardTitle;

    private String boardContents;

    private String boardAuthor;

    private Integer readCount;

    private String passWd;

    private String fileGroupId;

    @ManyToOne(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "useridx")
    @JsonIgnore
    private Member member;

    @BatchSize(size = 100)
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Comment>commentList = new ArrayList<>();

    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Like> likes = new LinkedHashSet<>();

    //게시글이 삭제되면 첨부파일도 같이 삭제가 된다.
    //여기서는 CascadeType.REMOVE 와 orphanRemoval = true 차이점 알아보기.
    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Attach>attachList = new ArrayList<>();

    @Builder
    public Board(Integer id, String boardContents, String boardAuthor, String boardTitle, Integer readCount, String passWd, String fileGroupId, Member member){
        this.id = id;
        this.boardTitle = boardTitle;
        this.boardAuthor = member.getUserId();
        this.boardContents = boardContents;
        this.readCount = readCount;
        this.passWd = passWd;
        this.fileGroupId = fileGroupId;
        this.member = member;
        this.getCreatedTime();
        this.getUpdatedTime();
    }

    //게시글 수정(Dirty Checking)
    public void boardUpdate(BoardRequestDto dto){
        this.boardTitle = dto.getBoardTitle();
        this.boardContents = dto.getBoardContents();
    }

    //파일 첨부
    public void addAttach(Attach attachFile){
        this.attachList.add(attachFile);
        if(attachFile.getBoard()!=this){
            attachFile.setBoard(this);
        }
    }
}

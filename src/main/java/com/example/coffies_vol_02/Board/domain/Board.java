package com.example.coffies_vol_02.Board.domain;

import com.example.coffies_vol_02.Attach.domain.Attach;
import com.example.coffies_vol_02.Board.domain.dto.BoardDto;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.Member.domain.Member;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Proxy(lazy = false)
@ToString(exclude = {"commentList","likes","attachList","member"})
@NoArgsConstructor
@Table(name = "tbl_board",
        indexes = {
                @Index(name = "board_index1",columnList = "boardTitle"),
                @Index(name = "board_index2",columnList = "boardAuthor")})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    private Member member;

    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment>commentList = new ArrayList<>();

    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    //게시글이 삭제되면 첨부파일도 같이 삭제가 된다.
    //여기서는 CascadeType.REMOVE 와 orphanRemoval = true 차이점 알아보기.
    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
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
    public void boardUpdate(BoardDto.BoardRequestDto dto){
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

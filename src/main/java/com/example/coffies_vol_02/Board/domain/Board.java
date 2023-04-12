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

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "tbl_board")
public class Board extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String boardTitle;

    private String boardContents;

    private String boardAuthor;

    private Integer readCount;

    private String passWd;

    private String fileGroupId;

    @Column(nullable = false)
    private Integer liked;//추천수

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    private Member member;

    @OneToMany(mappedBy = "board",fetch = FetchType.LAZY,cascade = CascadeType.ALL,orphanRemoval = true)
    @ToString.Exclude
    private List<Comment>commentList = new ArrayList<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL)
    private Set<Like> likes = new HashSet<>();

    @ToString.Exclude
    @OneToMany(mappedBy = "board",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Attach>attachList = new ArrayList<>();

    @Builder
    public Board(Integer id, String boardContents, String boardTitle, Integer readCount, String passWd, String fileGroupId, Member member,Like likes){
        this.id = id;
        this.boardTitle = boardTitle;
        this.boardAuthor = member.getUserId();
        this.boardContents = boardContents;
        this.readCount = readCount;
        this.passWd = passWd;
        this.fileGroupId = fileGroupId;
        this.member = member;
        this.liked = 0;
    }
    //게시글 수정
    public void boardUpdate(BoardDto.BoardRequestDto dto){
        this.boardTitle = dto.getBoardTitle();
        this.boardContents = dto.getBoardContents();
    }
    //게시글 좋아요 추가 +1
    public void increaseLikeCount(){
        this.liked +=1;
    }
    //게시글 좋아요 감소 -1
    public void decreaseLikeCount(){
        this.liked -=1;
    }
    //파일 첨부
    public void addAttach(Attach attachFile){
        this.attachList.add(attachFile);
        if(attachFile.getBoard()!=this){
            attachFile.setBoard(this);
        }
    }

}

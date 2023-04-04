package com.example.coffies_vol_02.Like.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Member.domain.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Getter
@Entity
@Table(name = "tbl_like")
@RequiredArgsConstructor
public class Like {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id",nullable = false)
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private Comment comment;

    @Column
    private boolean likeStatus;
    @Builder
    public Like(Member member, Board board) {
        this.board = board;
        this.member = member;
        this.likeStatus = true;
    }
    @Builder
    public Like(Member member,Comment comment){
        this.member = member;
        this.comment = comment;
        this.likeStatus =true;
    }

}

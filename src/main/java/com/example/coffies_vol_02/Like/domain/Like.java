package com.example.coffies_vol_02.Like.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Member.domain.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;

@Getter
@Entity
@Proxy(lazy = false)
@Table(name = "tbl_like")
@ToString(exclude = {"member","board"})
@AllArgsConstructor
@RequiredArgsConstructor
public class Like implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    @Builder
    public Like(Member member, Board board) {
        this.board = board;
        this.member = member;
    }
}

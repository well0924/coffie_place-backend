package com.example.coffies_vol_02.like.domain;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@RequiredArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id",column = @Column(name = "id"))})
public class Like extends BaseTime implements Serializable {

    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @JsonIgnore
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Member member;

    @Builder
    public Like(Member member, Board board) {
        this.board = board;
        this.member = member;
    }
}

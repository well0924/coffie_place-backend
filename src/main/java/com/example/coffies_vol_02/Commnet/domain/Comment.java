package com.example.coffies_vol_02.Commnet.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.member.domain.Member;
import lombok.*;

import javax.persistence.*;

@Getter
@Entity
@ToString
@Table(name = "tbl_board_reply")
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String replyWriter;
    private String replyContents;
    private String replyPoint;
    private String replyLike;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    private Member member;

    @Builder
    public Comment(String replyWriter,String replyContents,String replyLike,String replyPoint,Board board,Member member){
        this.board = board;
        this.member = member;
        this.replyWriter = member.getUserId();
        this.replyContents = replyContents;
        this.replyLike = replyLike;
        this.replyPoint = replyPoint;
    }
}

package com.example.coffies_vol_02.Commnet.domain;

import com.example.coffies_vol_02.Board.domain.Board;
import com.example.coffies_vol_02.Config.BaseTime;
import com.example.coffies_vol_02.Like.domain.CommentLike;
import com.example.coffies_vol_02.Like.domain.Like;
import com.example.coffies_vol_02.Member.domain.Member;
import com.example.coffies_vol_02.Place.domain.Place;
import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Getter
@Entity
@ToString(exclude = {"member","board","place","likes"})
@Table(name = "tbl_board_reply")
@NoArgsConstructor
@AllArgsConstructor
public class Comment extends BaseTime {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String replyWriter;
    private String replyContents;
    private Integer replyPoint;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;
    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL)
    private Set<CommentLike> likes = new HashSet<>();

    @Builder
    public Comment(Integer id,String replyWriter,String replyContents,Integer replyPoint,Board board,Member member,Place place){
        this.id = id;
        this.board = board;
        this.member = member;
        this.place = place;
        this.replyPoint = replyPoint;
        this.replyWriter = member.getUserId();
        this.replyContents = replyContents;
    }
}

package com.example.coffies_vol_02.commnet.domain;

import com.example.coffies_vol_02.board.domain.Board;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.like.domain.CommentLike;
import com.example.coffies_vol_02.member.domain.Member;
import com.example.coffies_vol_02.place.domain.Place;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Proxy;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Proxy(lazy = false)
@Entity
@ToString(exclude = {"member","board","place","likes"})
@Table(name = "tbl_reply")
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id",column = @Column(name = "id"))})
public class Comment extends BaseTime implements Serializable {

    private Integer id;

    private String replyWriter;

    private String replyContents;

    private Double replyPoint;
    //가게 댓글 좋아요
    @ColumnDefault("0")
    private Integer likeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "place_id")
    private Place place;

    @BatchSize(size = 1000)
    @OneToMany(mappedBy = "comment",cascade = CascadeType.ALL)
    private Set<CommentLike> likes = new HashSet<>();

    @Builder
    public Comment(Integer id,String replyWriter,String replyContents,Double replyPoint,Integer likeCount,Board board,Member member,Place place){
        this.id = id;
        this.board = board;
        this.member = member;
        this.place = place;
        this.replyPoint = replyPoint;
        this.replyWriter = member.getUserId();
        this.replyContents = replyContents;
        this.likeCount = 0;
    }

    /**
     * 가게 댓글 좋아요 증가
     **/
    public  void commentLikeUp(){
        this.likeCount++;
    }

    /**
     * 가게 댓글 좋아요 감소
     **/
    public void commentLikeDown(){
        this.likeCount--;
    }
}

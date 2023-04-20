package com.example.coffies_vol_02.Like.domain;

import com.example.coffies_vol_02.Commnet.domain.Comment;
import com.example.coffies_vol_02.Member.domain.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@ToString
@Table(name = "tbl_comment_like")
@NoArgsConstructor
@AllArgsConstructor
public class CommentLike {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id")
    private Comment comment;

    @Builder
    public CommentLike(Member member ,Comment comment){
        this.member = member;
        this.comment = comment;
    }
}

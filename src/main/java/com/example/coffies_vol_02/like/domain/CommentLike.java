package com.example.coffies_vol_02.like.domain;

import com.example.coffies_vol_02.commnet.domain.Comment;
import com.example.coffies_vol_02.config.BaseTime;
import com.example.coffies_vol_02.member.domain.Member;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Getter
@Table(name = "tbl_comment_like")
@NoArgsConstructor
@AttributeOverrides({@AttributeOverride(name = "id",column = @Column(name = "id"))})
public class CommentLike extends BaseTime {

    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "useridx")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonIgnore
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="comment_id")
    @JsonIgnore
    private Comment comment;

    @Builder
    public CommentLike(Member member ,Comment comment){
        this.member = member;
        this.comment = comment;
    }
}

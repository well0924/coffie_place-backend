package com.example.coffies_vol_02.Commnet.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QComment is a Querydsl query type for Comment
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QComment extends EntityPathBase<Comment> {

    private static final long serialVersionUID = -1303620243L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QComment comment = new QComment("comment");

    public final com.example.coffies_vol_02.Config.QBaseTime _super = new com.example.coffies_vol_02.Config.QBaseTime(this);

    public final com.example.coffies_vol_02.Board.domain.QBoard board;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final SetPath<com.example.coffies_vol_02.Like.domain.CommentLike, com.example.coffies_vol_02.Like.domain.QCommentLike> likes = this.<com.example.coffies_vol_02.Like.domain.CommentLike, com.example.coffies_vol_02.Like.domain.QCommentLike>createSet("likes", com.example.coffies_vol_02.Like.domain.CommentLike.class, com.example.coffies_vol_02.Like.domain.QCommentLike.class, PathInits.DIRECT2);

    public final com.example.coffies_vol_02.Member.domain.QMember member;

    public final com.example.coffies_vol_02.Place.domain.QPlace place;

    public final StringPath replyContents = createString("replyContents");

    public final NumberPath<Integer> replyPoint = createNumber("replyPoint", Integer.class);

    public final StringPath replyWriter = createString("replyWriter");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QComment(String variable) {
        this(Comment.class, forVariable(variable), INITS);
    }

    public QComment(Path<? extends Comment> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QComment(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QComment(PathMetadata metadata, PathInits inits) {
        this(Comment.class, metadata, inits);
    }

    public QComment(Class<? extends Comment> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new com.example.coffies_vol_02.Board.domain.QBoard(forProperty("board"), inits.get("board")) : null;
        this.member = inits.isInitialized("member") ? new com.example.coffies_vol_02.Member.domain.QMember(forProperty("member")) : null;
        this.place = inits.isInitialized("place") ? new com.example.coffies_vol_02.Place.domain.QPlace(forProperty("place")) : null;
    }

}


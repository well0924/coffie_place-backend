package com.example.coffies_vol_02.board.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = 1147974239L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final com.example.coffies_vol_02.config.QBaseTime _super = new com.example.coffies_vol_02.config.QBaseTime(this);

    public final ListPath<com.example.coffies_vol_02.attach.domain.Attach, com.example.coffies_vol_02.attach.domain.QAttach> attachList = this.<com.example.coffies_vol_02.attach.domain.Attach, com.example.coffies_vol_02.attach.domain.QAttach>createList("attachList", com.example.coffies_vol_02.attach.domain.Attach.class, com.example.coffies_vol_02.attach.domain.QAttach.class, PathInits.DIRECT2);

    public final StringPath boardAuthor = createString("boardAuthor");

    public final StringPath boardContents = createString("boardContents");

    public final StringPath boardTitle = createString("boardTitle");

    public final ListPath<com.example.coffies_vol_02.commnet.domain.Comment, com.example.coffies_vol_02.commnet.domain.QComment> commentList = this.<com.example.coffies_vol_02.commnet.domain.Comment, com.example.coffies_vol_02.commnet.domain.QComment>createList("commentList", com.example.coffies_vol_02.commnet.domain.Comment.class, com.example.coffies_vol_02.commnet.domain.QComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath fileGroupId = createString("fileGroupId");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final SetPath<com.example.coffies_vol_02.like.domain.Like, com.example.coffies_vol_02.like.domain.QLike> liked = this.<com.example.coffies_vol_02.like.domain.Like, com.example.coffies_vol_02.like.domain.QLike>createSet("liked", com.example.coffies_vol_02.like.domain.Like.class, com.example.coffies_vol_02.like.domain.QLike.class, PathInits.DIRECT2);

    public final com.example.coffies_vol_02.member.domain.QMember member;

    public final StringPath passWd = createString("passWd");

    public final NumberPath<Integer> readCount = createNumber("readCount", Integer.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.coffies_vol_02.member.domain.QMember(forProperty("member")) : null;
    }

}


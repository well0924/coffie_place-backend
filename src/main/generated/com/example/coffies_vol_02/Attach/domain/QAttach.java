package com.example.coffies_vol_02.Attach.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QAttach is a Querydsl query type for Attach
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAttach extends EntityPathBase<Attach> {

    private static final long serialVersionUID = -518154367L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QAttach attach = new QAttach("attach");

    public final com.example.coffies_vol_02.Config.QBaseTime _super = new com.example.coffies_vol_02.Config.QBaseTime(this);

    public final com.example.coffies_vol_02.Board.domain.QBoard board;

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath filePath = createString("filePath");

    public final NumberPath<Long> fileSize = createNumber("fileSize", Long.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.example.coffies_vol_02.Notice.domain.QNoticeBoard noticeBoard;

    public final StringPath originFileName = createString("originFileName");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QAttach(String variable) {
        this(Attach.class, forVariable(variable), INITS);
    }

    public QAttach(Path<? extends Attach> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QAttach(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QAttach(PathMetadata metadata, PathInits inits) {
        this(Attach.class, metadata, inits);
    }

    public QAttach(Class<? extends Attach> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.board = inits.isInitialized("board") ? new com.example.coffies_vol_02.Board.domain.QBoard(forProperty("board"), inits.get("board")) : null;
        this.noticeBoard = inits.isInitialized("noticeBoard") ? new com.example.coffies_vol_02.Notice.domain.QNoticeBoard(forProperty("noticeBoard")) : null;
    }

}


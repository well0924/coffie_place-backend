package com.example.coffies_vol_02.notice.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QNoticeBoard is a Querydsl query type for NoticeBoard
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QNoticeBoard extends EntityPathBase<NoticeBoard> {

    private static final long serialVersionUID = -1500070945L;

    public static final QNoticeBoard noticeBoard = new QNoticeBoard("noticeBoard");

    public final com.example.coffies_vol_02.config.QBaseTime _super = new com.example.coffies_vol_02.config.QBaseTime(this);

    public final ListPath<com.example.coffies_vol_02.attach.domain.Attach, com.example.coffies_vol_02.attach.domain.QAttach> attachList = this.<com.example.coffies_vol_02.attach.domain.Attach, com.example.coffies_vol_02.attach.domain.QAttach>createList("attachList", com.example.coffies_vol_02.attach.domain.Attach.class, com.example.coffies_vol_02.attach.domain.QAttach.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath fileGroupId = createString("fileGroupId");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ComparablePath<Character> isFixed = createComparable("isFixed", Character.class);

    public final StringPath noticeContents = createString("noticeContents");

    public final StringPath noticeGroup = createString("noticeGroup");

    public final StringPath noticeTitle = createString("noticeTitle");

    public final StringPath noticeWriter = createString("noticeWriter");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QNoticeBoard(String variable) {
        super(NoticeBoard.class, forVariable(variable));
    }

    public QNoticeBoard(Path<? extends NoticeBoard> path) {
        super(path.getType(), path.getMetadata());
    }

    public QNoticeBoard(PathMetadata metadata) {
        super(NoticeBoard.class, metadata);
    }

}


package com.example.coffies_vol_02.FavoritePlace.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QFavoritePlace is a Querydsl query type for FavoritePlace
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QFavoritePlace extends EntityPathBase<FavoritePlace> {

    private static final long serialVersionUID = 965987231L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QFavoritePlace favoritePlace = new QFavoritePlace("favoritePlace");

    public final StringPath fileGroupId = createString("fileGroupId");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final com.example.coffies_vol_02.Member.domain.QMember member;

    public final com.example.coffies_vol_02.Place.domain.QPlace place;

    public QFavoritePlace(String variable) {
        this(FavoritePlace.class, forVariable(variable), INITS);
    }

    public QFavoritePlace(Path<? extends FavoritePlace> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QFavoritePlace(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QFavoritePlace(PathMetadata metadata, PathInits inits) {
        this(FavoritePlace.class, metadata, inits);
    }

    public QFavoritePlace(Class<? extends FavoritePlace> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new com.example.coffies_vol_02.Member.domain.QMember(forProperty("member")) : null;
        this.place = inits.isInitialized("place") ? new com.example.coffies_vol_02.Place.domain.QPlace(forProperty("place")) : null;
    }

}


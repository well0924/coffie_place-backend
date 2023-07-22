package com.example.coffies_vol_02.member.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMember is a Querydsl query type for Member
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMember extends EntityPathBase<Member> {

    private static final long serialVersionUID = -302455989L;

    public static final QMember member = new QMember("member1");

    public final com.example.coffies_vol_02.config.QBaseTime _super = new com.example.coffies_vol_02.config.QBaseTime(this);

    public final BooleanPath accountNonLocked = createBoolean("accountNonLocked");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final BooleanPath enabled = createBoolean("enabled");

    public final NumberPath<Integer> failedAttempt = createNumber("failedAttempt", Integer.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final DateTimePath<java.util.Date> lockTime = createDateTime("lockTime", java.util.Date.class);

    public final NumberPath<Double> memberLat = createNumber("memberLat", Double.class);

    public final NumberPath<Double> memberLng = createNumber("memberLng", Double.class);

    public final StringPath memberName = createString("memberName");

    public final StringPath password = createString("password");

    public final EnumPath<com.example.coffies_vol_02.config.constant.Role> role = createEnum("role", com.example.coffies_vol_02.config.constant.Role.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public final StringPath userAddr1 = createString("userAddr1");

    public final StringPath userAddr2 = createString("userAddr2");

    public final StringPath userAge = createString("userAge");

    public final StringPath userEmail = createString("userEmail");

    public final StringPath userGender = createString("userGender");

    public final StringPath userId = createString("userId");

    public final StringPath userPhone = createString("userPhone");

    public final ListPath<com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace, com.example.coffies_vol_02.favoritePlace.domain.QFavoritePlace> wishList = this.<com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace, com.example.coffies_vol_02.favoritePlace.domain.QFavoritePlace>createList("wishList", com.example.coffies_vol_02.favoritePlace.domain.FavoritePlace.class, com.example.coffies_vol_02.favoritePlace.domain.QFavoritePlace.class, PathInits.DIRECT2);

    public QMember(String variable) {
        super(Member.class, forVariable(variable));
    }

    public QMember(Path<? extends Member> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMember(PathMetadata metadata) {
        super(Member.class, metadata);
    }

}


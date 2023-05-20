package com.example.coffies_vol_02.place.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlace is a Querydsl query type for Place
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlace extends EntityPathBase<Place> {

    private static final long serialVersionUID = -1322258433L;

    public static final QPlace place = new QPlace("place");

    public final com.example.coffies_vol_02.config.QBaseTime _super = new com.example.coffies_vol_02.config.QBaseTime(this);

    public final ListPath<com.example.coffies_vol_02.commnet.domain.Comment, com.example.coffies_vol_02.commnet.domain.QComment> commentList = this.<com.example.coffies_vol_02.commnet.domain.Comment, com.example.coffies_vol_02.commnet.domain.QComment>createList("commentList", com.example.coffies_vol_02.commnet.domain.Comment.class, com.example.coffies_vol_02.commnet.domain.QComment.class, PathInits.DIRECT2);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath fileGroupId = createString("fileGroupId");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath placeAddr1 = createString("placeAddr1");

    public final StringPath placeAddr2 = createString("placeAddr2");

    public final StringPath placeAuthor = createString("placeAuthor");

    public final StringPath placeClose = createString("placeClose");

    public final ListPath<PlaceImage, QPlaceImage> placeImageList = this.<PlaceImage, QPlaceImage>createList("placeImageList", PlaceImage.class, QPlaceImage.class, PathInits.DIRECT2);

    public final NumberPath<Double> placeLat = createNumber("placeLat", Double.class);

    public final NumberPath<Double> placeLng = createNumber("placeLng", Double.class);

    public final StringPath placeName = createString("placeName");

    public final StringPath placePhone = createString("placePhone");

    public final StringPath placeStart = createString("placeStart");

    public final NumberPath<Double> reviewRate = createNumber("reviewRate", Double.class);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QPlace(String variable) {
        super(Place.class, forVariable(variable));
    }

    public QPlace(Path<? extends Place> path) {
        super(path.getType(), path.getMetadata());
    }

    public QPlace(PathMetadata metadata) {
        super(Place.class, metadata);
    }

}


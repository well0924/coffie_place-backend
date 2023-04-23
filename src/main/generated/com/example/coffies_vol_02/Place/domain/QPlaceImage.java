package com.example.coffies_vol_02.Place.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QPlaceImage is a Querydsl query type for PlaceImage
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QPlaceImage extends EntityPathBase<PlaceImage> {

    private static final long serialVersionUID = -74069892L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QPlaceImage placeImage = new QPlaceImage("placeImage");

    public final com.example.coffies_vol_02.Config.QBaseTime _super = new com.example.coffies_vol_02.Config.QBaseTime(this);

    //inherited
    public final DateTimePath<java.time.LocalDateTime> createdTime = _super.createdTime;

    public final StringPath fileGroupId = createString("fileGroupId");

    public final StringPath fileType = createString("fileType");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath imgGroup = createString("imgGroup");

    public final StringPath imgPath = createString("imgPath");

    public final StringPath imgUploader = createString("imgUploader");

    public final StringPath isTitle = createString("isTitle");

    public final StringPath originName = createString("originName");

    public final QPlace place;

    public final StringPath storedName = createString("storedName");

    public final StringPath thumbFileImagePath = createString("thumbFileImagePath");

    public final StringPath thumbFilePath = createString("thumbFilePath");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updatedTime = _super.updatedTime;

    public QPlaceImage(String variable) {
        this(PlaceImage.class, forVariable(variable), INITS);
    }

    public QPlaceImage(Path<? extends PlaceImage> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QPlaceImage(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QPlaceImage(PathMetadata metadata, PathInits inits) {
        this(PlaceImage.class, metadata, inits);
    }

    public QPlaceImage(Class<? extends PlaceImage> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.place = inits.isInitialized("place") ? new QPlace(forProperty("place")) : null;
    }

}


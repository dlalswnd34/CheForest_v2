package com.simplecoding.cheforest.jpa.dust.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QDustCache is a Querydsl query type for DustCache
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QDustCache extends EntityPathBase<DustCache> {

    private static final long serialVersionUID = -794002696L;

    public static final QDustCache dustCache = new QDustCache("dustCache");

    public final StringPath dataTime = createString("dataTime");

    public final StringPath pm10 = createString("pm10");

    public final StringPath pm10Grade = createString("pm10Grade");

    public final StringPath pm25 = createString("pm25");

    public final StringPath pm25Grade = createString("pm25Grade");

    public final StringPath resultCode = createString("resultCode");

    public final StringPath sido = createString("sido");

    public QDustCache(String variable) {
        super(DustCache.class, forVariable(variable));
    }

    public QDustCache(Path<? extends DustCache> path) {
        super(path.getType(), path.getMetadata());
    }

    public QDustCache(PathMetadata metadata) {
        super(DustCache.class, metadata);
    }

}


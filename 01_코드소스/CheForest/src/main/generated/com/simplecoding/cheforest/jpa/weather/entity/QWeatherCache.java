package com.simplecoding.cheforest.jpa.weather.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QWeatherCache is a Querydsl query type for WeatherCache
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QWeatherCache extends EntityPathBase<WeatherCache> {

    private static final long serialVersionUID = 1326437814L;

    public static final QWeatherCache weatherCache = new QWeatherCache("weatherCache");

    public final StringPath baseDate = createString("baseDate");

    public final StringPath baseTime = createString("baseTime");

    public final DateTimePath<java.time.LocalDateTime> dataTime = createDateTime("dataTime", java.time.LocalDateTime.class);

    public final StringPath humidity = createString("humidity");

    public final StringPath region = createString("region");

    public final StringPath resultCode = createString("resultCode");

    public final StringPath sky = createString("sky");

    public final StringPath temperature = createString("temperature");

    public QWeatherCache(String variable) {
        super(WeatherCache.class, forVariable(variable));
    }

    public QWeatherCache(Path<? extends WeatherCache> path) {
        super(path.getType(), path.getMetadata());
    }

    public QWeatherCache(PathMetadata metadata) {
        super(WeatherCache.class, metadata);
    }

}


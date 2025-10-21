package com.simplecoding.cheforest.jpa.season.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QSeasonIngredient is a Querydsl query type for SeasonIngredient
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QSeasonIngredient extends EntityPathBase<SeasonIngredient> {

    private static final long serialVersionUID = 1243127805L;

    public static final QSeasonIngredient seasonIngredient = new QSeasonIngredient("seasonIngredient");

    public final StringPath description = createString("description");

    public final StringPath effects = createString("effects");

    public final StringPath imageUrl = createString("imageUrl");

    public final NumberPath<Long> ingredientId = createNumber("ingredientId", Long.class);

    public final StringPath name = createString("name");

    public final StringPath seasonDetail = createString("seasonDetail");

    public final StringPath seasons = createString("seasons");

    public QSeasonIngredient(String variable) {
        super(SeasonIngredient.class, forVariable(variable));
    }

    public QSeasonIngredient(Path<? extends SeasonIngredient> path) {
        super(path.getType(), path.getMetadata());
    }

    public QSeasonIngredient(PathMetadata metadata) {
        super(SeasonIngredient.class, metadata);
    }

}


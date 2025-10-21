package com.simplecoding.cheforest.jpa.recipe.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRecipe is a Querydsl query type for Recipe
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecipe extends EntityPathBase<Recipe> {

    private static final long serialVersionUID = 1793818402L;

    public static final QRecipe recipe = new QRecipe("recipe");

    public final StringPath area = createString("area");

    public final StringPath categoryEn = createString("categoryEn");

    public final StringPath categoryKr = createString("categoryKr");

    public final StringPath cookTime = createString("cookTime");

    public final StringPath difficulty = createString("difficulty");

    public final StringPath dustGood = createString("dustGood");

    public final StringPath ingredientEn = createString("ingredientEn");

    public final StringPath ingredientKr = createString("ingredientKr");

    public final StringPath instructionEn = createString("instructionEn");

    public final StringPath instructionKr = createString("instructionKr");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final StringPath measureEn = createString("measureEn");

    public final StringPath measureKr = createString("measureKr");

    public final StringPath recipeId = createString("recipeId");

    public final StringPath season = createString("season");

    public final StringPath thumbnail = createString("thumbnail");

    public final StringPath titleEn = createString("titleEn");

    public final StringPath titleKr = createString("titleKr");

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public QRecipe(String variable) {
        super(Recipe.class, forVariable(variable));
    }

    public QRecipe(Path<? extends Recipe> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecipe(PathMetadata metadata) {
        super(Recipe.class, metadata);
    }

}


package com.simplecoding.cheforest.jpa.board.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QBoard is a Querydsl query type for Board
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QBoard extends EntityPathBase<Board> {

    private static final long serialVersionUID = -127937876L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QBoard board = new QBoard("board");

    public final com.simplecoding.cheforest.jpa.common.QBaseTimeEntity _super = new com.simplecoding.cheforest.jpa.common.QBaseTimeEntity(this);

    public final NumberPath<Long> boardId = createNumber("boardId", Long.class);

    public final StringPath category = createString("category");

    public final StringPath content = createString("content");

    public final StringPath cookTime = createString("cookTime");

    public final StringPath difficulty = createString("difficulty");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> insertTime = _super.insertTime;

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final StringPath prepare = createString("prepare");

    public final StringPath prepareAmount = createString("prepareAmount");

    public final StringPath thumbnail = createString("thumbnail");

    public final StringPath title = createString("title");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

    public final NumberPath<Long> viewCount = createNumber("viewCount", Long.class);

    public final com.simplecoding.cheforest.jpa.auth.entity.QMember writer;

    public QBoard(String variable) {
        this(Board.class, forVariable(variable), INITS);
    }

    public QBoard(Path<? extends Board> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QBoard(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QBoard(PathMetadata metadata, PathInits inits) {
        this(Board.class, metadata, inits);
    }

    public QBoard(Class<? extends Board> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.writer = inits.isInitialized("writer") ? new com.simplecoding.cheforest.jpa.auth.entity.QMember(forProperty("writer")) : null;
    }

}


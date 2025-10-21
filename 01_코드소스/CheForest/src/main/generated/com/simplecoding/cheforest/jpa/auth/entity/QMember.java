package com.simplecoding.cheforest.jpa.auth.entity;

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

    private static final long serialVersionUID = 1301368072L;

    public static final QMember member = new QMember("member1");

    public final com.simplecoding.cheforest.jpa.common.QBaseTimeEntity _super = new com.simplecoding.cheforest.jpa.common.QBaseTimeEntity(this);

    public final StringPath email = createString("email");

    public final StringPath grade = createString("grade");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> insertTime = _super.insertTime;

    public final DateTimePath<java.util.Date> lastLoginTime = createDateTime("lastLoginTime", java.util.Date.class);

    public final StringPath loginId = createString("loginId");

    public final NumberPath<Long> memberIdx = createNumber("memberIdx", Long.class);

    public final ListPath<com.simplecoding.cheforest.jpa.chat.entity.Message, com.simplecoding.cheforest.jpa.chat.entity.QMessage> messages = this.<com.simplecoding.cheforest.jpa.chat.entity.Message, com.simplecoding.cheforest.jpa.chat.entity.QMessage>createList("messages", com.simplecoding.cheforest.jpa.chat.entity.Message.class, com.simplecoding.cheforest.jpa.chat.entity.QMessage.class, PathInits.DIRECT2);

    public final StringPath nickname = createString("nickname");

    public final StringPath password = createString("password");

    public final NumberPath<Long> point = createNumber("point", Long.class);

    public final StringPath profile = createString("profile");

    public final StringPath provider = createString("provider");

    public final EnumPath<Member.Role> role = createEnum("role", Member.Role.class);

    public final StringPath socialId = createString("socialId");

    public final StringPath suspension = createString("suspension");

    public final StringPath tempPasswordYn = createString("tempPasswordYn");

    //inherited
    public final DateTimePath<java.time.LocalDateTime> updateTime = _super.updateTime;

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


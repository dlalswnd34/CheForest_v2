package com.simplecoding.cheforest.jpa.inquiries.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QInquiries is a Querydsl query type for Inquiries
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QInquiries extends EntityPathBase<Inquiries> {

    private static final long serialVersionUID = 1992104204L;

    public static final QInquiries inquiries = new QInquiries("inquiries");

    public final DateTimePath<java.util.Date> answerAt = createDateTime("answerAt", java.util.Date.class);

    public final StringPath answerContent = createString("answerContent");

    public final StringPath answerStatus = createString("answerStatus");

    public final DateTimePath<java.util.Date> createdAt = createDateTime("createdAt", java.util.Date.class);

    public final NumberPath<Long> inquiryId = createNumber("inquiryId", Long.class);

    public final StringPath isFaq = createString("isFaq");

    public final NumberPath<Long> likeCount = createNumber("likeCount", Long.class);

    public final NumberPath<Long> memberIdx = createNumber("memberIdx", Long.class);

    public final StringPath questionContent = createString("questionContent");

    public final StringPath title = createString("title");

    public QInquiries(String variable) {
        super(Inquiries.class, forVariable(variable));
    }

    public QInquiries(Path<? extends Inquiries> path) {
        super(path.getType(), path.getMetadata());
    }

    public QInquiries(PathMetadata metadata) {
        super(Inquiries.class, metadata);
    }

}


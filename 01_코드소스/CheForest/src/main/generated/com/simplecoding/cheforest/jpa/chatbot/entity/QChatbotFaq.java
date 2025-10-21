package com.simplecoding.cheforest.jpa.chatbot.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatbotFaq is a Querydsl query type for ChatbotFaq
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatbotFaq extends EntityPathBase<ChatbotFaq> {

    private static final long serialVersionUID = 559804330L;

    public static final QChatbotFaq chatbotFaq = new QChatbotFaq("chatbotFaq");

    public final StringPath answer = createString("answer");

    public final StringPath category = createString("category");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath question = createString("question");

    public QChatbotFaq(String variable) {
        super(ChatbotFaq.class, forVariable(variable));
    }

    public QChatbotFaq(Path<? extends ChatbotFaq> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatbotFaq(PathMetadata metadata) {
        super(ChatbotFaq.class, metadata);
    }

}


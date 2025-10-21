package com.simplecoding.cheforest.es.integratedSearch.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

// TODO : 엘라스틱서치 어노테이션
// TODO : @Document(indexName = "인덱스명(테이블명)")
@Document(indexName = "integrated_search", writeTypeHint = WriteTypeHint.FALSE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IntegratedSearch {
    @Id
    @ReadOnlyProperty  // 이 필드가 source에 쓰이지 않도록
    private String id;

    private String type;
    private String title;
    private String thumbnail;
    private String ingredients;
    private String category;
    private String cooktime;
    private String difficulty;
}

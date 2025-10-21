package com.simplecoding.cheforest.es.integratedSearch.service;

import com.simplecoding.cheforest.es.integratedSearch.dto.IntegratedSearchDto;
import com.simplecoding.cheforest.es.integratedSearch.entity.IntegratedSearch;
import com.simplecoding.cheforest.jpa.common.MapStruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IntegratedSearchService {

    private final ElasticsearchOperations elasticsearchOperations; // TODO: querydsl 작성용 클래스
    private final MapStruct mapStruct;

    public Page<IntegratedSearchDto> search(String keyword, String type, Pageable pageable) {
        NativeQueryBuilder qb = new NativeQueryBuilder();

        // 기본 검색(제목/카테고리/재료)
        qb.withQuery(q -> q.bool(b -> {
            b.must(m -> m.multiMatch(mm -> mm
                    .fields("title", "category", "ingredients")
                    .query(keyword)
            ));
            // type이 all이 아니면 필터 적용
            if (!"all".equalsIgnoreCase(type)) {
                b.filter(f -> f.term(t -> t.field("type").value(type)));
            }
            return b;
        }));

        // 스코어 기준으로 내림차순
        qb.withSort(s -> s.score(sc -> sc.order(co.elastic.clients.elasticsearch._types.SortOrder.Desc)));

        qb.withPageable(pageable);

        Query query = qb.build();
        SearchHits<IntegratedSearch> hits = elasticsearchOperations.search(query, IntegratedSearch.class);

        List<IntegratedSearchDto> content = hits.getSearchHits().stream()
                .map(h -> mapStruct.toDto(h.getContent()))
                .collect(Collectors.toList());

        return new PageImpl<>(content, pageable, hits.getTotalHits());
    }

    public long recipeCount(String keyword) {
        Query query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.multiMatch(mm -> mm
                                .fields("title", "category", "ingredients")
                                .query(keyword)
                        ))
                        .filter(f -> f.term(t -> t.field("type").value("recipe")))
                ))
                .build();
        return elasticsearchOperations.count(query, IntegratedSearch.class);
    }

    public long boardCount(String keyword) {
        Query query = new NativeQueryBuilder()
                .withQuery(q -> q.bool(b -> b
                        .must(m -> m.multiMatch(mm -> mm
                                .fields("title", "category", "ingredients")
                                .query(keyword)
                        ))
                        .filter(f -> f.term(t -> t.field("type").value("board")))
                ))
                .build();
        return elasticsearchOperations.count(query, IntegratedSearch.class);
    }
//     수정, 추가
    public void saveData(IntegratedSearch integratedSearch) {
        IntegratedSearch entity = new IntegratedSearch();
        entity.setId("board-" + integratedSearch.getId());
        entity.setTitle(integratedSearch.getTitle());
        entity.setCategory(integratedSearch.getCategory());
        entity.setIngredients(integratedSearch.getIngredients());
        entity.setType("board");
        entity.setThumbnail(integratedSearch.getThumbnail());
        entity.setCooktime(integratedSearch.getCooktime());
        entity.setDifficulty(integratedSearch.getDifficulty());

        elasticsearchOperations.save(entity);  // 추가 또는 수정
    }
//     삭제
    public void deleteData(String id) {
        elasticsearchOperations.delete("board-" + id, IntegratedSearch.class);  // 삭제
    }

}

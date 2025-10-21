package com.simplecoding.cheforest.es.integratedSearch.repository;


import com.simplecoding.cheforest.es.integratedSearch.entity.IntegratedSearch;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IntegratedSearchRepository extends ElasticsearchRepository<IntegratedSearch, String> {

}

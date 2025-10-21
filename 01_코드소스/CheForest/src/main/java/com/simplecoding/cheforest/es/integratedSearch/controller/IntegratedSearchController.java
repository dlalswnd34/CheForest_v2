package com.simplecoding.cheforest.es.integratedSearch.controller;

import com.simplecoding.cheforest.es.integratedSearch.dto.IntegratedSearchDto;
import com.simplecoding.cheforest.es.integratedSearch.service.IntegratedSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class IntegratedSearchController {
    private final IntegratedSearchService integratedSearchService;

    //    통합검색
    @GetMapping("/search")
    public String search(@RequestParam(defaultValue = "") String totalKeyword,
                         @RequestParam(defaultValue = "all") String type,
                         @PageableDefault(page = 0, size = 12) Pageable pageable,
                         Model model) {
//        TODO: 1) 서비스의 search 메소드 실행
        Page<IntegratedSearchDto> pages = integratedSearchService.search(totalKeyword, type, pageable);

        model.addAttribute("recipeCount", integratedSearchService.recipeCount(totalKeyword));
        model.addAttribute("boardCount", integratedSearchService.boardCount(totalKeyword));
        model.addAttribute("totalKeyword", totalKeyword);
        model.addAttribute("pages", pages);
        model.addAttribute("searches", pages.getContent()); // 결과 배열
        model.addAttribute("type", type); // JSP active 표시용

        return "search/searchAll";
    }


}

package com.simplecoding.cheforest.jpa.board.controller;

import com.simplecoding.cheforest.jpa.board.dto.BoardListDto;
import com.simplecoding.cheforest.jpa.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class BoardRestController {

    private final BoardService boardService;

    // 인기 게시글 (좋아요 수 기준 Top5)
    @GetMapping("/api/boards/popular")
    public List<BoardListDto> findPopularBoards() {
        return boardService.findPopularBoards();
    }

    // 게시글 검색
    @GetMapping("/api/boards/search")
    public List<BoardListDto> searchBoards(@RequestParam String keyword) {
        return boardService.searchBoards(keyword);
    }

    // 사용자 레시피 랜덤 추천 (카테고리별)
    @GetMapping("/api/boards/random")
    public BoardListDto getRandomUserRecipe(@RequestParam String category) {
        return boardService.getRandomBoardByCategory(category);
    }
}

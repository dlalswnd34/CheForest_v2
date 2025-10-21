package com.simplecoding.cheforest.jpa.like.controller;


import com.simplecoding.cheforest.jpa.like.dto.LikeRes;
import com.simplecoding.cheforest.jpa.like.dto.LikeSaveReq;
import com.simplecoding.cheforest.jpa.like.service.LikeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/like")
@RequiredArgsConstructor
public class LikeController {

    private final LikeService likeService;

    /** 👍 좋아요 등록 */
    @PostMapping("/add")
    public LikeRes addLike(@RequestBody LikeSaveReq req) {
        log.info("📥 addLike 요청: {}", req);
        return likeService.addLike(req);
    }

    /** ❌ 좋아요 취소 */
    @PostMapping("/remove")
    public LikeRes removeLike(@RequestBody LikeSaveReq req) {
        log.info("📥 removeLike 요청: {}", req);
        return likeService.removeLike(req);
    }

    /** 📊 좋아요 수 조회 */
    @GetMapping("/count")
    public long getLikeCount(@RequestParam(required = false) Long boardId,
                             @RequestParam(required = false) String recipeId,
                             @RequestParam String likeType) {
        if ("BOARD".equalsIgnoreCase(likeType) && boardId != null) {
            return likeService.countBoardLikes(boardId);
        } else if ("RECIPE".equalsIgnoreCase(likeType) && recipeId != null) {
            return likeService.countRecipeLikes(recipeId);
        }
        return 0L;
    }

    /** 🔍 좋아요 여부 확인 */
    @GetMapping("/check")
    public boolean checkLike(@RequestParam Long memberIdx,
                             @RequestParam String likeType,
                             @RequestParam(required = false) Long boardId,
                             @RequestParam(required = false) String recipeId,
                             @RequestParam(required = false) Long reviewId) {

        if ("BOARD".equalsIgnoreCase(likeType) && boardId != null) {
            return likeService.existsBoardLike(memberIdx, boardId);
        } else if ("RECIPE".equalsIgnoreCase(likeType) && recipeId != null) {
            return likeService.existsRecipeLike(memberIdx, recipeId);
        } else if ("REVIEW".equalsIgnoreCase(likeType) && reviewId != null) {
            return likeService.existsReviewLike(memberIdx, reviewId);
        }
        return false;
    }

    /** ✅ CheForest 레시피 좋아요 목록 (마이페이지용) */
    @GetMapping("/api/mypage/liked/recipes")
    public List<LikeRes> getLikedRecipes(
            @RequestParam Long memberIdx
    ) {
        return likeService.getLikesByMember(memberIdx, "RECIPE");
    }

    /** ✅ 사용자 작성 게시글 좋아요 목록 (마이페이지용) */
    @GetMapping("/api/mypage/liked/posts")
    public List<LikeRes> getLikedBoards(
            @RequestParam Long memberIdx
    ) {
        return likeService.getLikesByMember(memberIdx, "BOARD");
    }
}
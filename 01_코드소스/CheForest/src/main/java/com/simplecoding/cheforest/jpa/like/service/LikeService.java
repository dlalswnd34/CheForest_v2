package com.simplecoding.cheforest.jpa.like.service;

import com.simplecoding.cheforest.jpa.common.MapStruct;
import com.simplecoding.cheforest.jpa.board.repository.BoardRepository;
import com.simplecoding.cheforest.jpa.like.dto.LikeRes;
import com.simplecoding.cheforest.jpa.like.dto.LikeSaveReq;
import com.simplecoding.cheforest.jpa.like.entity.Like;
import com.simplecoding.cheforest.jpa.like.repository.LikeRepository;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.auth.repository.MemberRepository;
import com.simplecoding.cheforest.jpa.recipe.repository.RecipeRepository;
import com.simplecoding.cheforest.jpa.review.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final LikeRepository likeRepository;
    private final BoardRepository boardRepository;
    private final RecipeRepository recipeRepository;
    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final MapStruct mapStruct;

    /** 👍 좋아요 등록 */
    public LikeRes addLike(LikeSaveReq req) {
        // 1. 회원 조회
        Member member = memberRepository.findById(req.getMemberIdx())
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 2. DTO → Entity 변환 (MapStruct)
        Like like = mapStruct.toEntity(req, member);
        likeRepository.save(like);

        // 3. 카운트 갱신
        long latestCount;
        if ("BOARD".equalsIgnoreCase(req.getLikeType())) {
            likeRepository.increaseBoardLikeCount(req.getBoardId());
            latestCount = boardRepository.findById(req.getBoardId())
                    .orElseThrow().getLikeCount();

        } else if ("RECIPE".equalsIgnoreCase(req.getLikeType())) {
            likeRepository.increaseRecipeLikeCount(req.getRecipeId());
            latestCount = recipeRepository.findById(req.getRecipeId())
                    .orElseThrow().getLikeCount();

        } else if ("REVIEW".equalsIgnoreCase(req.getLikeType())) {   // ✅ 댓글 좋아요
            likeRepository.increaseReviewLikeCount(req.getReviewId());
            latestCount = reviewRepository.findById(req.getReviewId())
                    .orElseThrow().getLikeCount();

        } else {
            throw new IllegalArgumentException("지원하지 않는 LikeType: " + req.getLikeType());
        }

        // 4. Entity → DTO 변환 (MapStruct)
        LikeRes res = mapStruct.toDto(like);
        res.setLikeCount(latestCount);  // 최신 카운트 반영

        return res;
    }

    /** ❌ 좋아요 취소 */
    public LikeRes removeLike(LikeSaveReq req) {
        Member member = memberRepository.findById(req.getMemberIdx()).orElseThrow();

        if ("BOARD".equalsIgnoreCase(req.getLikeType())) {
            likeRepository.deleteByMemberAndBoardId(member, req.getBoardId());
            likeRepository.decreaseBoardLikeCount(req.getBoardId());
            long latestCount = boardRepository.findById(req.getBoardId())
                    .orElseThrow().getLikeCount();

            return LikeRes.builder()
                    .likeType("BOARD")
                    .boardId(req.getBoardId())
                    .likeCount(latestCount)
                    .build();

        } else if ("RECIPE".equalsIgnoreCase(req.getLikeType())) {
            likeRepository.deleteByMemberAndRecipeId(member, req.getRecipeId());
            likeRepository.decreaseRecipeLikeCount(req.getRecipeId());
            long latestCount = recipeRepository.findById(req.getRecipeId())
                    .orElseThrow().getLikeCount();

            return LikeRes.builder()
                    .likeType("RECIPE")
                    .recipeId(req.getRecipeId())
                    .likeCount(latestCount)
                    .build();

        } else if ("REVIEW".equalsIgnoreCase(req.getLikeType())) {  // ✅ 댓글 좋아요 취소
            likeRepository.deleteByMemberAndReviewId(member, req.getReviewId());
            likeRepository.decreaseReviewLikeCount(req.getReviewId());
            long latestCount = reviewRepository.findById(req.getReviewId())
                    .orElseThrow().getLikeCount();

            return LikeRes.builder()
                    .likeType("REVIEW")
                    .reviewId(req.getReviewId())
                    .likeCount(latestCount)
                    .build();

        } else {
            throw new IllegalArgumentException("지원하지 않는 LikeType: " + req.getLikeType());
        }
    }

    /** 📊 좋아요 수 조회 */
    public long countBoardLikes(Long boardId) {
        return boardRepository.findById(boardId)
                .orElseThrow().getLikeCount();
    }

    public long countRecipeLikes(String recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow().getLikeCount();
    }

    /** 🔍 좋아요 여부 확인 */
    public boolean existsBoardLike(Long memberIdx, Long boardId) {
        Member member = memberRepository.findById(memberIdx).orElseThrow();
        return likeRepository.existsByMemberAndBoardId(member, boardId);
    }

    public boolean existsRecipeLike(Long memberIdx, String recipeId) {
        Member member = memberRepository.findById(memberIdx).orElseThrow();
        return likeRepository.existsByMemberAndRecipeId(member, recipeId);
    }

    public boolean existsReviewLike(Long memberIdx, Long reviewId) {
        Member member = memberRepository.findById(memberIdx).orElseThrow();
        return likeRepository.existsByMemberAndReviewId(member, reviewId);
    }

    /** ✅ 게시글 삭제 시 좋아요 전체 삭제 */
    public void deleteAllByBoardId(Long boardId) {
        likeRepository.deleteAllByBoardId(boardId);
    }

    /** 📋 마이페이지용 좋아요 조회 */
    public List<LikeRes> getLikesByMember(Long memberIdx, String likeType) {
        Member member = memberRepository.findById(memberIdx).orElseThrow();

        List<Like> likes;
        switch (likeType.toUpperCase()) {
            case "BOARD":
                likes = likeRepository.findAllBoardLikesByMember(member);
                break;
            case "RECIPE":
                likes = likeRepository.findAllRecipeLikesByMember(member);
                break;
            default:
                likes = likeRepository.findAllByMember(member);
        }

        return likes.stream()
                .map(mapStruct::toDto)
                .collect(Collectors.toList());
    }
}

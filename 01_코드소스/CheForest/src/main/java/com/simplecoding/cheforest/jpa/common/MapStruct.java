package com.simplecoding.cheforest.jpa.common;

import com.simplecoding.cheforest.es.integratedSearch.dto.IntegratedSearchDto;
import com.simplecoding.cheforest.es.integratedSearch.entity.IntegratedSearch;
import com.simplecoding.cheforest.jpa.auth.dto.MemberDetailDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberSignupDto;
import com.simplecoding.cheforest.jpa.chatbot.dto.ChatbotFaqDto;
import com.simplecoding.cheforest.jpa.chatbot.entity.ChatbotFaq;
import com.simplecoding.cheforest.jpa.file.entity.File;
import com.simplecoding.cheforest.jpa.board.dto.BoardListDto;
import com.simplecoding.cheforest.jpa.board.dto.BoardSaveReq;
import com.simplecoding.cheforest.jpa.like.dto.LikeRes;
import com.simplecoding.cheforest.jpa.like.dto.LikeSaveReq;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeCardDTO;
import com.simplecoding.cheforest.jpa.review.dto.ReviewDto;
import org.mapstruct.*;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.board.entity.Board;
import com.simplecoding.cheforest.jpa.review.entity.Review;
import com.simplecoding.cheforest.jpa.file.dto.FileDto;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import com.simplecoding.cheforest.jpa.recipe.entity.Recipe;
import com.simplecoding.cheforest.jpa.like.entity.Like;


@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface MapStruct {
    // ================= Member =================
    // 회원가입 DTO -> Member 엔티티
    Member toEntity(MemberSignupDto dto);

    // Member 엔티티 -> 상세 DTO (세션/조회용)
    MemberDetailDto toDetailDto(Member member);

    // ================= Recipe =================
    RecipeDto toDto(Recipe entity);

    @Mapping(target = "id", source = "recipeId")
    @Mapping(target = "title", source = "titleKr")
    @Mapping(target = "thumbnail", source = "thumbnail")
    @Mapping(target = "categoryName", source = "categoryKr")
    @Mapping(target = "writerNickname", constant = "CheForest 관리자")
    @Mapping(target = "cookTime", source = "cookTime")
    @Mapping(target = "difficulty", source = "difficulty")
    @Mapping(target = "viewCount", source = "viewCount")
    @Mapping(target = "likeCount", source = "likeCount")
    RecipeCardDTO toCardDto(Recipe entity);

    // ================= Board =================
    // 목록 조회 DTO 변환
    @Mapping(target = "nickname", source = "writer.nickname")
    @Mapping(target = "writerIdx", source = "writer.memberIdx")
    @Mapping(target = "likeCount", source = "likeCount")   // 좋아요 수 매핑
    BoardListDto toListDto(Board board);

    // 저장용 DTO → 엔티티
    @Mapping(target = "boardId", ignore = true)   // PK 자동 생성
    @Mapping(target = "writer", ignore = true)    // 서비스에서 Member 주입
    @Mapping(target = "viewCount", constant = "0L")
    @Mapping(target = "likeCount", constant = "0L")
    @Mapping(target = "thumbnail", ignore = true) // 파일 업로드 후 서비스에서 세팅
//  prepare / prepareAmount는 Service에서 문자열로 합쳐서 세팅할 예정
    Board toEntity(BoardSaveReq dto);

    // ================= Review =================
    // Review → ReviewDto
    @Mapping(source = "board.boardId", target = "boardId")   // Board 엔티티의 PK를 DTO로 변환
    ReviewDto toDto(Review entity);

    // ReviewDto → Review
    @Mapping(source = "boardId", target = "board.boardId")   // DTO의 boardId를 엔티티 Board 참조로 매핑
    Review toEntity(ReviewDto dto);
    // 유틸 함수 - 임시 닉네임 매핑
    default String getNickname(Long writerIdx) {
        // TODO: 실제 Member 엔티티에서 닉네임 조회하는 서비스와 연동
        return "익명";
    }

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromDto(ReviewDto dto, @MappingTarget Review entity);

    // ================= File =================
    // Entity → DTO
    @Mapping(source = "uploader.memberIdx", target = "uploaderIdx")
    @Mapping(source = "uploader.loginId", target = "uploaderLoginId")
    FileDto toDto(File file);

    //    TODO: 통합검색관련) IntegratedSearch <-> IntegratedSearchDto
    IntegratedSearchDto toDto(IntegratedSearch integratedSearch);
    IntegratedSearch toEntity(IntegratedSearchDto integratedSearchDto);
    @Mapping(target = "ingredients",
            expression = "java(com.simplecoding.cheforest.jpa.common.util.StringUtil.joinList(boardSaveReq.getIngredientName()))")
    IntegratedSearch boardToEntity(BoardSaveReq boardSaveReq);

    // -- chat bot ----
    // Entity <-> DTO
    ChatbotFaqDto toDto(ChatbotFaq chatbotFaq);
    ChatbotFaq toEntity(ChatbotFaqDto chatbotFaqDto);

    // ✅ DTO → Entity 변환
    @Mapping(target = "likeId", ignore = true)
    @Mapping(target = "likeDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "boardId", source = "req.boardId")
    @Mapping(target = "recipeId", source = "req.recipeId")
    @Mapping(target = "reviewId", source = "req.reviewId")
    @Mapping(target = "likeType", source = "req.likeType")
    @Mapping(target = "member", source = "member") // ✅ Member 객체 주입
    Like toEntity(LikeSaveReq req, Member member);

    // ✅ Entity → DTO 변환
    @Mapping(target = "memberIdx", source = "member.memberIdx")
    @Mapping(target = "nickname", source = "member.nickname")
    LikeRes toDto(Like like);
}



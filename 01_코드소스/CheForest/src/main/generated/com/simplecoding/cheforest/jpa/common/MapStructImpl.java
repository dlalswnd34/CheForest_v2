package com.simplecoding.cheforest.jpa.common;

import com.simplecoding.cheforest.es.integratedSearch.dto.IntegratedSearchDto;
import com.simplecoding.cheforest.es.integratedSearch.entity.IntegratedSearch;
import com.simplecoding.cheforest.jpa.auth.dto.MemberDetailDto;
import com.simplecoding.cheforest.jpa.auth.dto.MemberSignupDto;
import com.simplecoding.cheforest.jpa.auth.entity.Member;
import com.simplecoding.cheforest.jpa.board.dto.BoardListDto;
import com.simplecoding.cheforest.jpa.board.dto.BoardSaveReq;
import com.simplecoding.cheforest.jpa.board.entity.Board;
import com.simplecoding.cheforest.jpa.chatbot.dto.ChatbotFaqDto;
import com.simplecoding.cheforest.jpa.chatbot.entity.ChatbotFaq;
import com.simplecoding.cheforest.jpa.file.dto.FileDto;
import com.simplecoding.cheforest.jpa.file.entity.File;
import com.simplecoding.cheforest.jpa.recipe.dto.RecipeDto;
import com.simplecoding.cheforest.jpa.recipe.entity.Recipe;
import com.simplecoding.cheforest.jpa.review.dto.ReviewDto;
import com.simplecoding.cheforest.jpa.review.entity.Review;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-10-10T08:38:26+0900",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 17.0.16 (Azul Systems, Inc.)"
)
@Component
public class MapStructImpl implements MapStruct {

    @Override
    public Member toEntity(MemberSignupDto dto) {
        if ( dto == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.loginId( dto.getLoginId() );
        member.password( dto.getPassword() );
        member.email( dto.getEmail() );
        member.nickname( dto.getNickname() );

        return member.build();
    }

    @Override
    public MemberDetailDto toDetailDto(Member member) {
        if ( member == null ) {
            return null;
        }

        MemberDetailDto memberDetailDto = new MemberDetailDto();

        memberDetailDto.setMemberIdx( member.getMemberIdx() );
        memberDetailDto.setLoginId( member.getLoginId() );
        memberDetailDto.setEmail( member.getEmail() );
        memberDetailDto.setNickname( member.getNickname() );
        memberDetailDto.setProfile( member.getProfile() );
        if ( member.getRole() != null ) {
            memberDetailDto.setRole( member.getRole().name() );
        }

        return memberDetailDto;
    }

    @Override
    public BoardListDto toListDto(Board board) {
        if ( board == null ) {
            return null;
        }

        BoardListDto.BoardListDtoBuilder boardListDto = BoardListDto.builder();

        boardListDto.nickname( boardWriterNickname( board ) );
        boardListDto.writerIdx( boardWriterMemberIdx( board ) );
        boardListDto.likeCount( board.getLikeCount() );
        boardListDto.boardId( board.getBoardId() );
        boardListDto.category( board.getCategory() );
        boardListDto.title( board.getTitle() );
        boardListDto.viewCount( board.getViewCount() );
        boardListDto.thumbnail( board.getThumbnail() );
        boardListDto.insertTime( board.getInsertTime() );
        boardListDto.difficulty( board.getDifficulty() );

        return boardListDto.build();
    }

    @Override
    public Board toEntity(BoardSaveReq dto) {
        if ( dto == null ) {
            return null;
        }

        Board.BoardBuilder board = Board.builder();

        board.category( dto.getCategory() );
        board.title( dto.getTitle() );
        board.cookTime( dto.getCookTime() );
        board.difficulty( dto.getDifficulty() );

        board.viewCount( (long) 0L );
        board.likeCount( (long) 0L );

        return board.build();
    }

    @Override
    public ReviewDto toDto(Review entity) {
        if ( entity == null ) {
            return null;
        }

        ReviewDto.ReviewDtoBuilder reviewDto = ReviewDto.builder();

        reviewDto.boardId( entityBoardBoardId( entity ) );
        reviewDto.reviewId( entity.getReviewId() );
        reviewDto.writerIdx( entity.getWriterIdx() );
        reviewDto.content( entity.getContent() );
        reviewDto.insertTime( entity.getInsertTime() );
        reviewDto.updateTime( entity.getUpdateTime() );
        reviewDto.parentId( entity.getParentId() );

        return reviewDto.build();
    }

    @Override
    public Review toEntity(ReviewDto dto) {
        if ( dto == null ) {
            return null;
        }

        Review.ReviewBuilder review = Review.builder();

        review.board( reviewDtoToBoard( dto ) );
        review.reviewId( dto.getReviewId() );
        review.writerIdx( dto.getWriterIdx() );
        review.content( dto.getContent() );
        review.insertTime( dto.getInsertTime() );
        review.updateTime( dto.getUpdateTime() );
        review.parentId( dto.getParentId() );

        return review.build();
    }

    @Override
    public void updateEntityFromDto(ReviewDto dto, Review entity) {
        if ( dto == null ) {
            return;
        }

        if ( dto.getReviewId() != null ) {
            entity.setReviewId( dto.getReviewId() );
        }
        if ( dto.getWriterIdx() != null ) {
            entity.setWriterIdx( dto.getWriterIdx() );
        }
        if ( dto.getContent() != null ) {
            entity.setContent( dto.getContent() );
        }
        if ( dto.getInsertTime() != null ) {
            entity.setInsertTime( dto.getInsertTime() );
        }
        if ( dto.getUpdateTime() != null ) {
            entity.setUpdateTime( dto.getUpdateTime() );
        }
        if ( dto.getParentId() != null ) {
            entity.setParentId( dto.getParentId() );
        }
    }

    @Override
    public FileDto toDto(File file) {
        if ( file == null ) {
            return null;
        }

        FileDto fileDto = new FileDto();

        fileDto.setUploaderIdx( fileUploaderMemberIdx( file ) );
        fileDto.setUploaderLoginId( fileUploaderLoginId( file ) );
        fileDto.setFileId( file.getFileId() );
        fileDto.setFileName( file.getFileName() );
        fileDto.setFilePath( file.getFilePath() );
        fileDto.setFileType( file.getFileType() );
        fileDto.setUseType( file.getUseType() );
        fileDto.setUseTargetId( file.getUseTargetId() );
        fileDto.setUsePosition( file.getUsePosition() );
        fileDto.setInsertTime( file.getInsertTime() );
        fileDto.setUpdateTime( file.getUpdateTime() );

        return fileDto;
    }

    @Override
    public File toEntity(FileDto fileDto) {
        if ( fileDto == null ) {
            return null;
        }

        File.FileBuilder file = File.builder();

        file.uploader( fileDtoToMember( fileDto ) );
        file.fileId( fileDto.getFileId() );
        file.fileName( fileDto.getFileName() );
        file.filePath( fileDto.getFilePath() );
        file.fileType( fileDto.getFileType() );
        file.useType( fileDto.getUseType() );
        file.useTargetId( fileDto.getUseTargetId() );
        file.usePosition( fileDto.getUsePosition() );

        return file.build();
    }

    @Override
    public Recipe toEntity(RecipeDto dto) {
        if ( dto == null ) {
            return null;
        }

        Recipe.RecipeBuilder recipe = Recipe.builder();

        recipe.recipeId( dto.getRecipeId() );
        recipe.titleKr( dto.getTitleKr() );
        recipe.categoryKr( dto.getCategoryKr() );
        recipe.instructionKr( dto.getInstructionKr() );
        recipe.ingredientKr( dto.getIngredientKr() );
        recipe.measureKr( dto.getMeasureKr() );
        recipe.titleEn( dto.getTitleEn() );
        recipe.categoryEn( dto.getCategoryEn() );
        recipe.instructionEn( dto.getInstructionEn() );
        recipe.ingredientEn( dto.getIngredientEn() );
        recipe.measureEn( dto.getMeasureEn() );
        recipe.thumbnail( dto.getThumbnail() );
        recipe.area( dto.getArea() );
        recipe.likeCount( dto.getLikeCount() );
        recipe.viewCount( dto.getViewCount() );
        recipe.cookTime( dto.getCookTime() );
        recipe.difficulty( dto.getDifficulty() );

        return recipe.build();
    }

    @Override
    public RecipeDto toDto(Recipe entity) {
        if ( entity == null ) {
            return null;
        }

        RecipeDto.RecipeDtoBuilder recipeDto = RecipeDto.builder();

        recipeDto.recipeId( entity.getRecipeId() );
        recipeDto.titleKr( entity.getTitleKr() );
        recipeDto.categoryKr( entity.getCategoryKr() );
        recipeDto.instructionKr( entity.getInstructionKr() );
        recipeDto.ingredientKr( entity.getIngredientKr() );
        recipeDto.measureKr( entity.getMeasureKr() );
        recipeDto.titleEn( entity.getTitleEn() );
        recipeDto.categoryEn( entity.getCategoryEn() );
        recipeDto.instructionEn( entity.getInstructionEn() );
        recipeDto.ingredientEn( entity.getIngredientEn() );
        recipeDto.measureEn( entity.getMeasureEn() );
        recipeDto.thumbnail( entity.getThumbnail() );
        recipeDto.area( entity.getArea() );
        recipeDto.likeCount( entity.getLikeCount() );
        recipeDto.viewCount( entity.getViewCount() );
        recipeDto.difficulty( entity.getDifficulty() );
        recipeDto.cookTime( entity.getCookTime() );

        return recipeDto.build();
    }

    @Override
    public IntegratedSearchDto toDto(IntegratedSearch integratedSearch) {
        if ( integratedSearch == null ) {
            return null;
        }

        IntegratedSearchDto integratedSearchDto = new IntegratedSearchDto();

        integratedSearchDto.setId( integratedSearch.getId() );
        integratedSearchDto.setType( integratedSearch.getType() );
        integratedSearchDto.setTitle( integratedSearch.getTitle() );
        integratedSearchDto.setThumbnail( integratedSearch.getThumbnail() );
        integratedSearchDto.setIngredients( integratedSearch.getIngredients() );
        integratedSearchDto.setCategory( integratedSearch.getCategory() );
        integratedSearchDto.setCooktime( integratedSearch.getCooktime() );
        integratedSearchDto.setDifficulty( integratedSearch.getDifficulty() );

        return integratedSearchDto;
    }

    @Override
    public IntegratedSearch toEntity(IntegratedSearchDto integratedSearchDto) {
        if ( integratedSearchDto == null ) {
            return null;
        }

        IntegratedSearch integratedSearch = new IntegratedSearch();

        integratedSearch.setId( integratedSearchDto.getId() );
        integratedSearch.setType( integratedSearchDto.getType() );
        integratedSearch.setTitle( integratedSearchDto.getTitle() );
        integratedSearch.setThumbnail( integratedSearchDto.getThumbnail() );
        integratedSearch.setIngredients( integratedSearchDto.getIngredients() );
        integratedSearch.setCategory( integratedSearchDto.getCategory() );
        integratedSearch.setCooktime( integratedSearchDto.getCooktime() );
        integratedSearch.setDifficulty( integratedSearchDto.getDifficulty() );

        return integratedSearch;
    }

    @Override
    public IntegratedSearch boardToEntity(BoardSaveReq boardSaveReq) {
        if ( boardSaveReq == null ) {
            return null;
        }

        IntegratedSearch integratedSearch = new IntegratedSearch();

        integratedSearch.setTitle( boardSaveReq.getTitle() );
        integratedSearch.setCategory( boardSaveReq.getCategory() );
        integratedSearch.setDifficulty( boardSaveReq.getDifficulty() );

        integratedSearch.setIngredients( com.simplecoding.cheforest.jpa.common.util.StringUtil.joinList(boardSaveReq.getIngredientName()) );

        return integratedSearch;
    }

    @Override
    public ChatbotFaqDto toDto(ChatbotFaq chatbotFaq) {
        if ( chatbotFaq == null ) {
            return null;
        }

        ChatbotFaqDto.ChatbotFaqDtoBuilder chatbotFaqDto = ChatbotFaqDto.builder();

        chatbotFaqDto.id( chatbotFaq.getId() );
        chatbotFaqDto.question( chatbotFaq.getQuestion() );
        chatbotFaqDto.answer( chatbotFaq.getAnswer() );
        chatbotFaqDto.category( chatbotFaq.getCategory() );

        return chatbotFaqDto.build();
    }

    @Override
    public ChatbotFaq toEntity(ChatbotFaqDto chatbotFaqDto) {
        if ( chatbotFaqDto == null ) {
            return null;
        }

        ChatbotFaq.ChatbotFaqBuilder chatbotFaq = ChatbotFaq.builder();

        chatbotFaq.id( chatbotFaqDto.getId() );
        chatbotFaq.question( chatbotFaqDto.getQuestion() );
        chatbotFaq.answer( chatbotFaqDto.getAnswer() );
        chatbotFaq.category( chatbotFaqDto.getCategory() );

        return chatbotFaq.build();
    }

    private String boardWriterNickname(Board board) {
        if ( board == null ) {
            return null;
        }
        Member writer = board.getWriter();
        if ( writer == null ) {
            return null;
        }
        String nickname = writer.getNickname();
        if ( nickname == null ) {
            return null;
        }
        return nickname;
    }

    private Long boardWriterMemberIdx(Board board) {
        if ( board == null ) {
            return null;
        }
        Member writer = board.getWriter();
        if ( writer == null ) {
            return null;
        }
        Long memberIdx = writer.getMemberIdx();
        if ( memberIdx == null ) {
            return null;
        }
        return memberIdx;
    }

    private Long entityBoardBoardId(Review review) {
        if ( review == null ) {
            return null;
        }
        Board board = review.getBoard();
        if ( board == null ) {
            return null;
        }
        Long boardId = board.getBoardId();
        if ( boardId == null ) {
            return null;
        }
        return boardId;
    }

    protected Board reviewDtoToBoard(ReviewDto reviewDto) {
        if ( reviewDto == null ) {
            return null;
        }

        Board.BoardBuilder board = Board.builder();

        board.boardId( reviewDto.getBoardId() );

        return board.build();
    }

    private Long fileUploaderMemberIdx(File file) {
        if ( file == null ) {
            return null;
        }
        Member uploader = file.getUploader();
        if ( uploader == null ) {
            return null;
        }
        Long memberIdx = uploader.getMemberIdx();
        if ( memberIdx == null ) {
            return null;
        }
        return memberIdx;
    }

    private String fileUploaderLoginId(File file) {
        if ( file == null ) {
            return null;
        }
        Member uploader = file.getUploader();
        if ( uploader == null ) {
            return null;
        }
        String loginId = uploader.getLoginId();
        if ( loginId == null ) {
            return null;
        }
        return loginId;
    }

    protected Member fileDtoToMember(FileDto fileDto) {
        if ( fileDto == null ) {
            return null;
        }

        Member.MemberBuilder member = Member.builder();

        member.memberIdx( fileDto.getUploaderIdx() );
        member.loginId( fileDto.getUploaderLoginId() );

        return member.build();
    }
}

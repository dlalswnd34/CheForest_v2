package com.simplecoding.cheforest.jpa.board.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BoardSaveReq {

    // 기본 정보
    private String title;
    private String category;
    private String difficulty;
    private String cookTime;

    // 대표 이미지
    private MultipartFile mainImage;

    // 재료
    private List<String> ingredientName;
    private List<String> ingredientAmount; // prepareAmount 와 매칭

    // 조리법
    private List<String> instructionContent;
    private List<MultipartFile> instructionImage;
}

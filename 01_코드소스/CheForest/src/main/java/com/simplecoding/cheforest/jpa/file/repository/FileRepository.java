package com.simplecoding.cheforest.jpa.file.repository;

import com.simplecoding.cheforest.jpa.file.entity.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    // 특정 대상(useTargetId)에 속한 파일들 (ex. BOARD, MEMBER 등)
    List<File> findByUseTypeAndUseTargetId(String useType, Long useTargetId);

    // 회원 프로필 최신 파일 (USE_TYPE = MEMBER + 최신 1건)
    Optional<File> findTop1ByUseTypeAndUseTargetIdOrderByInsertTimeDesc(String useType, Long useTargetId);

    // 대상 삭제 시 관련 파일 일괄 삭제
    void deleteByUseTargetIdAndUseType(Long useTargetId, String useType);
}

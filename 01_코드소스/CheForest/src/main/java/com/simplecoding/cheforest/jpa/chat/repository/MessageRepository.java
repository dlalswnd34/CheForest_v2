package com.simplecoding.cheforest.jpa.chat.repository;

import com.simplecoding.cheforest.jpa.chat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // messageDate 오름차순으로 정렬해서 모든 메시지 조회
    List<Message> findAllByOrderByMessageDateAsc();
}

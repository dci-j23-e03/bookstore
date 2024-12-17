package com.project.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.project.model.Reply;

import java.util.List;

public interface ReplyRepository extends JpaRepository<Reply, Long> {

    List<Reply> findByMessageIdOrderByRepliedAtDesc(Long messageId);
}
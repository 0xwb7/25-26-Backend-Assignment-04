package org.example.simplejwtexample.repository;

import org.example.simplejwtexample.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}

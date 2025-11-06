package org.example.simplejwtexample.repository;

import org.example.simplejwtexample.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    void deleteByAuthorId(Long authorId);
}

package org.example.simplejwtexample.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.dto.comment.request.CommentCreateRequest;
import org.example.simplejwtexample.dto.comment.request.CommentUpdateRequest;
import org.example.simplejwtexample.dto.comment.response.CommentResponse;
import org.example.simplejwtexample.service.CommentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @Valid @RequestBody CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, commentCreateRequest));
    }

    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable("id") Long commentId, @Valid @RequestBody CommentUpdateRequest commentUpdateRequest) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentUpdateRequest));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

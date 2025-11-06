package org.example.simplejwtexample.controller;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.dto.comment.request.CommentCreateRequest;
import org.example.simplejwtexample.dto.comment.request.CommentUpdateRequest;
import org.example.simplejwtexample.dto.comment.response.CommentResponse;
import org.example.simplejwtexample.service.CommentService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponse> createComment(@PathVariable Long postId, @RequestBody CommentCreateRequest commentCreateRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createComment(postId, commentCreateRequest));
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<Page<CommentResponse>> getCommentList(@PathVariable Long postId,
                                                                @RequestParam(defaultValue = "0") int page,
                                                                @RequestParam(defaultValue = "0") int size) {
        return ResponseEntity.ok(commentService.listByPost(postId, page, size));
    }


    @PatchMapping("/comments/{id}")
    public ResponseEntity<CommentResponse> updateComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequest commentUpdateRequest) {
        return ResponseEntity.ok(commentService.updateComment(commentId, commentUpdateRequest));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long id) {
        commentService.deleteComment(id);
        return ResponseEntity.noContent().build();
    }
}

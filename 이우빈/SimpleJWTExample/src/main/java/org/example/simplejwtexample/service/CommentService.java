package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.Comment;
import org.example.simplejwtexample.domain.Post;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.comment.request.CommentCreateRequest;
import org.example.simplejwtexample.dto.comment.request.CommentUpdateRequest;
import org.example.simplejwtexample.dto.comment.response.CommentResponse;
import org.example.simplejwtexample.repository.CommentRepository;
import org.example.simplejwtexample.repository.PostRepository;
import org.example.simplejwtexample.repository.UserRepository;
import org.example.simplejwtexample.validator.UserValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest) {
        Long currentId = requiredLogin();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));
        User author = userRepository.findById(currentId)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));
        Comment savedComment = commentRepository.save(Comment.builder()
                .post(post)
                .author(author)
                .content(commentCreateRequest.getContent())
                .build());

        return CommentResponse.commentInfo(savedComment);
    }

    public Page<CommentResponse> listByPost(Long postId, int page, int size) {
        Page<Comment> pageResponse = commentRepository.findByPostId(
                postId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"))
        );

        return pageResponse.map(CommentResponse::commentInfo);
    }

    public CommentResponse updateComment(Long id, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
        checkOwnerOrAdmin(comment.getAuthor().getId());
        comment.updateComment(commentUpdateRequest.getContent());

        return CommentResponse.commentInfo(comment);
    }

    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 댓글입니다."));
        checkOwnerOrAdmin(comment.getAuthor().getId());
        commentRepository.delete(comment);
    }

    private Long requiredLogin() {
        Long id = UserValidator.currentUserIDorNull();

        if (id == null) {
            throw new RuntimeException("로그인이 필요한 서비스입니다.");
        }

        return id;
    }

    private void checkOwnerOrAdmin(Long id) {
        Long myPost = requiredLogin();

        if (!myPost.equals(id) && !UserValidator.isAdmin()) {
            throw new RuntimeException("수정/삭제 권한이 없습니다.");
        }
    }
}

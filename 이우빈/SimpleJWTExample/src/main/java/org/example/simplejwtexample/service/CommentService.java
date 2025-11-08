package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.Comment;
import org.example.simplejwtexample.domain.Post;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.comment.request.CommentCreateRequest;
import org.example.simplejwtexample.dto.comment.request.CommentUpdateRequest;
import org.example.simplejwtexample.dto.comment.response.CommentResponse;
import org.example.simplejwtexample.exception.BadRequestException;
import org.example.simplejwtexample.exception.ErrorMessage;
import org.example.simplejwtexample.exception.NotFoundException;
import org.example.simplejwtexample.repository.CommentRepository;
import org.example.simplejwtexample.repository.PostRepository;
import org.example.simplejwtexample.repository.UserRepository;
import org.example.simplejwtexample.validator.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    @Transactional
    public CommentResponse createComment(Long postId, CommentCreateRequest commentCreateRequest) {
        Long currentId = requiredLogin();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));
        User author = userRepository.findById(currentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));
        Comment savedComment = commentRepository.save(Comment.builder()
                .post(post)
                .author(author)
                .content(commentCreateRequest.getContent())
                .build());

        return CommentResponse.commentInfo(savedComment);
    }

    @Transactional
    public CommentResponse updateComment(Long id, CommentUpdateRequest commentUpdateRequest) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));
        checkOwnerOrAdmin(comment.getAuthor().getId());
        comment.updateComment(commentUpdateRequest.getContent());

        return CommentResponse.commentInfo(comment);
    }

    @Transactional
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_COMMENT));
        checkOwnerOrAdmin(comment.getAuthor().getId());
        commentRepository.delete(comment);
    }

    private Long requiredLogin() {
        Long id = UserValidator.currentUserIDorNull();

        if (id == null) {
            throw new BadRequestException(ErrorMessage.NEED_TO_LOGIN);
        }

        return id;
    }

    private void checkOwnerOrAdmin(Long id) {
        Long myPost = requiredLogin();

        if (!myPost.equals(id) && !UserValidator.isAdmin()) {
            throw new BadRequestException(ErrorMessage.NO_PERMISSION);
        }
    }
}

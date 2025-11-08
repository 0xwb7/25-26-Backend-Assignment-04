package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.example.simplejwtexample.domain.Comment;
import org.example.simplejwtexample.domain.Post;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.post.request.PostCreateRequest;
import org.example.simplejwtexample.dto.post.request.PostUpdateRequest;
import org.example.simplejwtexample.dto.post.response.PostResponse;
import org.example.simplejwtexample.exception.BadRequestException;
import org.example.simplejwtexample.exception.ErrorMessage;
import org.example.simplejwtexample.exception.NotFoundException;
import org.example.simplejwtexample.repository.CommentRepository;
import org.example.simplejwtexample.repository.PostRepository;
import org.example.simplejwtexample.repository.UserRepository;
import org.example.simplejwtexample.validator.UserValidator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Transactional
    public PostResponse createPost(PostCreateRequest postCreateRequest) {
        Long currentId = requiredLogin();
        User author = userRepository.findById(currentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_USER));

        Post savedPost = postRepository.save(Post.builder()
                .author(author)
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .build());

        return PostResponse.postInfo(savedPost);
    }

    @Transactional(readOnly = true)
    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));

        List<Comment> comments = commentRepository.findByPostIdOrderByIdAsc(id);

        return PostResponse.postInfoWithComments(post, comments);
    }

    @Transactional
    public PostResponse updatePost(Long id, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));
        checkOwnerOrAdmin(post.getAuthor().getId());
        post.updatePost(postUpdateRequest.getTitle(), postUpdateRequest.getContent());

        return PostResponse.postInfo(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.NOT_EXIST_POST));
        checkOwnerOrAdmin(post.getAuthor().getId());

        commentRepository.deleteByPostId(id);
        postRepository.delete(post);
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

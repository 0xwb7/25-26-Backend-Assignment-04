package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.example.simplejwtexample.domain.Post;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.post.request.PostCreateRequest;
import org.example.simplejwtexample.dto.post.request.PostUpdateRequest;
import org.example.simplejwtexample.dto.post.response.PostResponse;
import org.example.simplejwtexample.exception.BadRequestException;
import org.example.simplejwtexample.exception.ErrorMessage;
import org.example.simplejwtexample.repository.PostRepository;
import org.example.simplejwtexample.repository.UserRepository;
import org.example.simplejwtexample.validator.UserValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostResponse createPost(PostCreateRequest postCreateRequest) {
        Long currentId = requiredLogin();
        User author = userRepository.findById(currentId)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_USER));

        Post savedPost = postRepository.save(Post.builder()
                .author(author)
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .build());

        return PostResponse.postInfo(savedPost);
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_POST));

        return PostResponse.postInfo(post);
    }

    public Page<PostResponse> postList(int page, int size) {
        Page<Post> pageResponse = postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

        return pageResponse.map(PostResponse::postInfo);
    }

    public PostResponse updatePost(Long id, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_POST));
        checkOwnerOrAdmin(post.getAuthor().getId());
        post.updatePost(postUpdateRequest.getTitle(), postUpdateRequest.getContent());

        return PostResponse.postInfo(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new BadRequestException(ErrorMessage.NOT_EXIST_POST));
        checkOwnerOrAdmin(post.getAuthor().getId());
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

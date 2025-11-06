package org.example.simplejwtexample.service;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.example.simplejwtexample.domain.Post;
import org.example.simplejwtexample.domain.User;
import org.example.simplejwtexample.dto.post.request.PostCreateRequest;
import org.example.simplejwtexample.dto.post.request.PostUpdateRequest;
import org.example.simplejwtexample.dto.post.response.PostResponse;
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
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다."));

        Post savedPost = postRepository.save(Post.builder()
                .author(author)
                .title(postCreateRequest.getTitle())
                .content(postCreateRequest.getContent())
                .build());

        return PostResponse.postInfo(savedPost);
    }

    public PostResponse getPost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));

        return PostResponse.postInfo(post);
    }

    public Page<PostResponse> postList(int page, int size) {
        Page<Post> pageResponse = postRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")));

        return pageResponse.map(PostResponse::postInfo);
    }

    public PostResponse updatePost(Long id, PostUpdateRequest postUpdateRequest) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));
        checkOwnerOrAdmin(post.getAuthor().getId());
        post.updatePost(postUpdateRequest.getTitle(), postUpdateRequest.getContent());

        return PostResponse.postInfo(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("존재하지 않는 글입니다."));
        checkOwnerOrAdmin(post.getAuthor().getId());
        postRepository.delete(post);
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

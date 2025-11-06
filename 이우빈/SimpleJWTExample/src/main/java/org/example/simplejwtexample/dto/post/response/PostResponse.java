package org.example.simplejwtexample.dto.post.response;

import lombok.Builder;
import lombok.Getter;
import org.example.simplejwtexample.domain.Post;

@Getter
@Builder
public class PostResponse {
    private Long postId;
    private Long authorId;
    private String title;
    private String content;

    public static PostResponse postInfo(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .authorId(post.getAuthor().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }
}

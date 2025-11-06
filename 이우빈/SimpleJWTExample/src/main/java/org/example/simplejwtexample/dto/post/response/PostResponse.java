package org.example.simplejwtexample.dto.post.response;

import lombok.Builder;
import lombok.Getter;
import org.example.simplejwtexample.domain.Comment;
import org.example.simplejwtexample.domain.Post;
import org.example.simplejwtexample.dto.comment.response.CommentResponse;

import java.util.List;

@Getter
@Builder
public class PostResponse {
    private Long postId;
    private String authorName;
    private String title;
    private String content;
    private List<CommentResponse> comments;

    public static PostResponse postInfo(Post post) {
        return PostResponse.builder()
                .postId(post.getId())
                .authorName(post.getAuthor().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .build();
    }

    public static PostResponse postInfoWithComments(Post post, List<Comment> comments) {
        return PostResponse.builder()
                .postId(post.getId())
                .authorName(post.getAuthor().getName())
                .title(post.getTitle())
                .content(post.getContent())
                .comments(comments.stream()
                        .map(CommentResponse::commentInfo)
                        .toList())
                .build();
    }
}

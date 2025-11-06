package org.example.simplejwtexample.dto.comment.response;

import lombok.Builder;
import lombok.Getter;
import org.example.simplejwtexample.domain.Comment;

@Getter
@Builder
public class CommentResponse {
    private Long commentId;
    private Long postId;
    private Long authorId;
    private String authorName;
    private String content;

    public static CommentResponse commentInfo(Comment comment) {
        return CommentResponse.builder()
                .commentId(comment.getId())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthor().getId())
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .build();
    }
}

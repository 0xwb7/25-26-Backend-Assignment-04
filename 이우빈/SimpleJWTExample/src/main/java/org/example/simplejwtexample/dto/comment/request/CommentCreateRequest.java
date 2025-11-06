package org.example.simplejwtexample.dto.comment.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "댓글 내용은 비어있을 수 없습니다.")
    private String content;
}

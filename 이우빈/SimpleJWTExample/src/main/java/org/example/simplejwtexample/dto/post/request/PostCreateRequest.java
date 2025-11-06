package org.example.simplejwtexample.dto.post.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PostCreateRequest {

    @NotNull(message = "글 제목은 비어있을 수 없습니다.")
    private String title;

    @NotNull(message = "글 내용은 비어있을 수 없습니다.")
    private String content;
}

package com.vikram.blogapp.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class CommentDTO {
    private long id;

    @NotEmpty
    private String content;

    @NotNull
    private Long userId;

    @NotNull
    private Long postId;
}

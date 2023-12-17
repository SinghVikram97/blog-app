package com.vikram.blogapp.dto;

import com.vikram.blogapp.entities.Category;
import com.vikram.blogapp.entities.User;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class PostDTO {
    private long id;

    @NotEmpty
    private String title;

    @NotEmpty
    private String content;

    private String imageName;

    private Date addedDate;

    @NotNull
    private Long userId;

    private Long categoryId;
}

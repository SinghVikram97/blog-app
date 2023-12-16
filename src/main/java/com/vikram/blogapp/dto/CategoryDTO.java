package com.vikram.blogapp.dto;

import jakarta.validation.constraints.NotEmpty;
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
public class CategoryDTO {
    private long id;

    @NotEmpty(message = "Category title should not be empty")
    private String categoryTitle;

    @NotEmpty(message = "Category description should not be empty")
    private String categoryDescription;

}

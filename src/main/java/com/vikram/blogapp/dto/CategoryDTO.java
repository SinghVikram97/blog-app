package com.vikram.blogapp.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

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

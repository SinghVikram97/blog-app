package com.vikram.blogapp.dto;

import jakarta.validation.constraints.*;
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
public class UserDTO {
    private long id;

    @NotEmpty
    @Size(min = 4, message = "Username must be minimum of 4 characters")
    private String name;

    @Email(message = "Email address is not valid")
    private String email;

    @NotEmpty
    @Size(min=3, max=10, message = "Password must be between 3-10 characters")
    private String password;

    @NotEmpty(message = "About should not be empty")
    private String about;
}

package com.vikram.blogapp.dto;

import com.vikram.blogapp.entities.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserRequest {
    @NotEmpty
    @Size(min = 4, message = "first name must be minimum of 4 characters")
    private String firstName;

    @NotEmpty
    @Size(min = 4, message = "last name must be minimum of 4 characters")
    private String lastName;

    @Email(message = "Email address is not valid")
    private String email;

    @NotEmpty
    @Size(min=3, max=10, message = "Password must be between 3-10 characters")
    private String password;

    @NotEmpty(message = "About should not be empty")
    private String about;

    @NotNull
    private Role role;
}

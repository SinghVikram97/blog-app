package com.vikram.blogapp.dto;

import lombok.*;

@NoArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
public class UserDTO {
    private long id;
    private String name;
    private String email;
    private String password;
    private String about;
}

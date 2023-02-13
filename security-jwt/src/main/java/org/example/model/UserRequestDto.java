package org.example.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String username;

    private String password;

    private String passwordCheck;
}

package org.example;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MemberRequestDto {
    private String email;
    private String password;
    private String nickname;

}

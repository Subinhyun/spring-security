package org.example.model;

import lombok.Data;
import lombok.Getter;

@Getter
@Data
public class LogoutRequestDto {
    private String username;
}
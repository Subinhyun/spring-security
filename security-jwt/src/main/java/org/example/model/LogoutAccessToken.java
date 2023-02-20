package org.example.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class LogoutAccessToken {

    @Id
    private String username;
    private Long expiration;

    private String accessToken;

}

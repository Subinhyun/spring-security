package org.example.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Getter
@NoArgsConstructor
public class RefreshToken {
    @Id
    public String id;
    public String token;

    public RefreshToken(RefreshToken refreshToken) {
        this.token = String.valueOf(refreshToken);
    }

    public RefreshToken updateValue(String refreshToken) {
        this.token = refreshToken;
        return this;
    }
}

package org.example.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
@Getter
public class RefreshToken {
    @Id
    public String id;
    public String value;

}

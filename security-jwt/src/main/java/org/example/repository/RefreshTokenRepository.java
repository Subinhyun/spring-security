package org.example.repository;

import org.example.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> deleteById(String id);
    boolean existsByValue(String value);

    RefreshToken getById(Authentication authentication);

}

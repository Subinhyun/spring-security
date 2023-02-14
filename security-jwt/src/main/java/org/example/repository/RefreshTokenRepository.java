package org.example.repository;

import org.example.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    RefreshToken deleteById(String id);
    RefreshToken deleteByValue(String id);
    boolean existsByValue(String value);
}

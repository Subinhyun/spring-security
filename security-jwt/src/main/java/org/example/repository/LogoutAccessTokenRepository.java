package org.example.repository;

import org.example.model.LogoutAccessToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LogoutAccessTokenRepository extends JpaRepository<LogoutAccessToken, Long> {
    boolean existsByUsername(String username);

    String findByUsername(String username);

    boolean findByAccessToken(String accessToken);

    boolean existsByAccessToken(String accessToken);

    String deleteByUsername(String username);
}

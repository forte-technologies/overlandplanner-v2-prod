package dev.forte.overlandplannerv2.jwtconfig;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {

    Optional<RefreshTokenEntity> findByToken(String token);

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity r WHERE r.username = :username")
    void deleteByUsername(@Param("username") String username);
}

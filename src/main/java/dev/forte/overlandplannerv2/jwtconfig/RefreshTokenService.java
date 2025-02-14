package dev.forte.overlandplannerv2.jwtconfig;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Transactional
    public String createRefreshToken(String username) {


        try {
            refreshTokenRepository.deleteByUsername(username);

        } catch (Exception e) {
            System.out.println("❌ Error deleting old refresh tokens: " + e.getMessage());
            e.printStackTrace();
        }

        RefreshTokenEntity refreshToken = new RefreshTokenEntity();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(Instant.now().plusSeconds(7 * 24 * 60 * 60)); // 7 days validity

        try {
            refreshTokenRepository.save(refreshToken);

        } catch (Exception e) {
            System.out.println("❌ Error saving refresh token: " + e.getMessage());
            e.printStackTrace();
        }

        return refreshToken.getToken();
    }

    public String validateRefreshToken(String refreshToken) {
        Optional<RefreshTokenEntity> storedToken = refreshTokenRepository.findByToken(refreshToken);

        if (storedToken.isPresent()) {
            RefreshTokenEntity tokenEntity = storedToken.get();

            // Check if the refresh token is expired
            if (tokenEntity.getExpiryDate().isBefore(Instant.now())) {
                refreshTokenRepository.delete(tokenEntity); // Delete expired token
                return null; // Token is expired
            }

            return tokenEntity.getUsername(); // Return username if token is valid
        }

        return null; // Token not found
    }


    public void revokeRefreshToken(String refreshToken) {
        refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }

    public String getUsernameFromToken(String refreshToken) {
        return refreshTokenRepository.findByToken(refreshToken)
                .map(RefreshTokenEntity::getUsername)
                .orElse(null);
    }
    @Transactional
    public void revokeAllTokensForUser(String username) {
        refreshTokenRepository.deleteByUsername(username);
        System.out.println("Revoked all refresh tokens for user: " + username);
    }


}

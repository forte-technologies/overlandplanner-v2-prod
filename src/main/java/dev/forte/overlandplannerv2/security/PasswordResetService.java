package dev.forte.overlandplannerv2.security;

import dev.forte.overlandplannerv2.email.EmailService;
import dev.forte.overlandplannerv2.user.UserEntity;
import dev.forte.overlandplannerv2.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Service
public class PasswordResetService {
    
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetService.class);
    
    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    
    @Value("${app.password-reset.token-expiration-minutes:30}")
    private int tokenExpirationMinutes;
    
    public PasswordResetService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Initiates the password reset process for a user with the given email.
     * 
     * @param email The email of the user
     * @return true if the reset token was created and email sent, false if user not found
     */
    @Transactional
    public boolean initiatePasswordReset(String email) {
        logger.info("Initiating password reset for email: {}", email);
        
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            logger.warn("Password reset requested for non-existent email: {}", email);
            return false;
        }
        
        UserEntity user = userOpt.get();
        
        // Check if there's an existing token and delete it
        tokenRepository.findByUser(user).ifPresent(tokenRepository::delete);
        
        // Create new token
        PasswordResetToken token = new PasswordResetToken(user, tokenExpirationMinutes);
        tokenRepository.save(token);
        
        // Send email with token
        emailService.sendPasswordResetEmail(user.getEmail(), token.getToken());
        
        logger.info("Password reset token created for user: {}", user.getUsername());
        return true;
    }
    
    /**
     * Completes the password reset process by validating the token and setting the new password.
     * 
     * @param token The password reset token
     * @param newPassword The new password
     * @return true if the password was reset successfully, false otherwise
     */
    @Transactional
    public boolean completePasswordReset(String token, String newPassword) {
        logger.info("Completing password reset for token: {}", token);
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            logger.warn("Password reset attempted with invalid token: {}", token);
            return false;
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        // Check if token is expired or already used
        if (resetToken.isExpired() || resetToken.isUsed()) {
            logger.warn("Password reset attempted with expired or used token: {}", token);
            return false;
        }
        
        // Get the user and update password
        UserEntity user = resetToken.getUser();
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        // Mark token as used
        resetToken.setUsed(true);
        tokenRepository.save(resetToken);
        
        logger.info("Password reset completed successfully for user: {}", user.getUsername());
        return true;
    }
    
    /**
     * Validates a password reset token without changing the password.
     * 
     * @param token The password reset token
     * @return true if the token is valid, false otherwise
     */
    public boolean validateToken(String token) {
        logger.info("Validating password reset token: {}", token);
        
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(token);
        if (tokenOpt.isEmpty()) {
            logger.warn("Invalid token: {}", token);
            return false;
        }
        
        PasswordResetToken resetToken = tokenOpt.get();
        
        boolean isValid = !resetToken.isExpired() && !resetToken.isUsed();
        
        if (!isValid) {
            logger.warn("Token is expired or used: {}", token);
        }
        
        return isValid;
    }
} 
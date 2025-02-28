package dev.forte.overlandplannerv2.email;

public interface EmailService {
    void sendPasswordResetEmail(String to, String resetToken);
} 
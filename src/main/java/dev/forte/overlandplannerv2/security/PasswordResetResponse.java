package dev.forte.overlandplannerv2.security;

public class PasswordResetResponse {
    
    private boolean success;
    private String message;
    
    public PasswordResetResponse() {
    }
    
    public PasswordResetResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public static PasswordResetResponse success(String message) {
        return new PasswordResetResponse(true, message);
    }
    
    public static PasswordResetResponse error(String message) {
        return new PasswordResetResponse(false, message);
    }
} 
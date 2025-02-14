package dev.forte.overlandplannerv2.jwtconfig;

public class AuthResponse {
    private String token;
    private String refreshToken;
    private String username;
    private String message;

    public AuthResponse(String token, String refreshToken, String username, String message) {
        this.token = token;
        this.refreshToken = refreshToken;
        this.username = username;
        this.message = message;
    }

    public String getToken() {
        return token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }
}

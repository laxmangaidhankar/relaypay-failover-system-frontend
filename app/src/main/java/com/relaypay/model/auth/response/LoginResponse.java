package com.relaypay.model.auth.response;
public class LoginResponse {

    private boolean success;
    private String message;

    private User user;

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public static class User {

        private String id;
        private String phone;
        private String walletId;

        public String getId() {
            return id;
        }

        public String getPhone() {
            return phone;
        }

        public String getWalletId() {
            return walletId;
        }
    }
}
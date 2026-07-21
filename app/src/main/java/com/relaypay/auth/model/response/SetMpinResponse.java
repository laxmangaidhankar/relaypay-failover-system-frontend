package com.relaypay.auth.model.response;

public class SetMpinResponse {

    private String message;
    private User user;

    private String accessToken;
    private String refreshToken;
    private long expiresIn;

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public String getUserId() {
        return user != null ? user.getId() : null;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public String getAccessToken() {
        return accessToken;
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
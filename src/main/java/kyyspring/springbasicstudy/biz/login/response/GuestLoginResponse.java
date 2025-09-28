package kyyspring.springbasicstudy.biz.login.response;

import lombok.Data;

@Data
public class GuestLoginResponse {
    private String token;
    private String type;
    private String userType;
    private String guestId;
    private String sessionKey;
    private Long expiresIn;
    private Long createdAt;

    // 기본 생성자
    public GuestLoginResponse() {}

    // 주요 생성자
    public GuestLoginResponse(String token, String guestId, String sessionKey) {
        this.token = token;
        this.type = "Bearer";
        this.userType = "guest";
        this.guestId = guestId;
        this.sessionKey = sessionKey;
        this.expiresIn = 1800L; // 30분 (초 단위)
        this.createdAt = System.currentTimeMillis();
    }

    // 전체 파라미터 생성자
    public GuestLoginResponse(String token, String guestId, String sessionKey, Long expiresIn) {
        this.token = token;
        this.type = "Bearer";
        this.userType = "guest";
        this.guestId = guestId;
        this.sessionKey = sessionKey;
        this.expiresIn = expiresIn;
        this.createdAt = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        return "GuestLoginResponse{" +
                "token='" + (token != null ? token.substring(0, Math.min(20, token.length())) + "..." : "null") + '\'' +
                ", type='" + type + '\'' +
                ", userType='" + userType + '\'' +
                ", guestId='" + guestId + '\'' +
                ", sessionKey='" + sessionKey + '\'' +
                ", expiresIn=" + expiresIn +
                ", createdAt=" + createdAt +
                '}';
    }
}
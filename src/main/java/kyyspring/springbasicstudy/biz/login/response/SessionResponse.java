package kyyspring.springbasicstudy.biz.login.response;

import lombok.Data;

@Data
public class SessionResponse {
    private String guestId;
    private String userType;
    private String sessionId;

    public SessionResponse(String guestId, String userType, String sessionId) {
        this.guestId = guestId;
        this.userType = userType;
        this.sessionId = sessionId;
    }

    // getter, setter
}
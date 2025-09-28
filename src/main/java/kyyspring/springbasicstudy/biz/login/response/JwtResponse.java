package kyyspring.springbasicstudy.biz.login.response;

import lombok.Data;

@Data
public class JwtResponse {
    private String token;
    private String type;
    private String userType;

    public JwtResponse(String token, String userType) {
        this.token = token;
        this.type = "Bearer";
        this.userType = userType;
    }

    // getter, setter
}
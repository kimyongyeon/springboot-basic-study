package kyyspring.springbasicstudy.countservice;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResult {
    private boolean success;
    private boolean locked;
    private String message;

    public static LoginResult success(String msg) {
        return new LoginResult(true, false, msg);
    }

    public static LoginResult fail(String msg) {
        return new LoginResult(false, false, msg);
    }

    public static LoginResult locked(String msg) {
        return new LoginResult(false, true, msg);
    }
}

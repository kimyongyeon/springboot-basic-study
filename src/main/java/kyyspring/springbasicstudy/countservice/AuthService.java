package kyyspring.springbasicstudy.countservice;

import kyyspring.springbasicstudy.biz.login.domain.User;
import kyyspring.springbasicstudy.biz.login.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final LoginFailService loginFailService;
    // private final PasswordEncoder passwordEncoder; 등등...

    public LoginResult login(String userId, String rawPassword) {

        // 1. 잠금 여부 선 체크
        if (loginFailService.isLocked(userId)) {
            return LoginResult.locked("5회 이상 실패로 오늘은 로그인이 잠겼습니다.");
        }

        // 2. 유저 조회
        User user = userRepository.findByUserId(userId)
                .orElse(null);

        if (user == null) {
            int failCount = loginFailService.increaseFailCount(userId);
            return LoginResult.fail("아이디 또는 비밀번호가 올바르지 않습니다. (실패 " + failCount + "회)");
        }

        // 3. 비밀번호 검증
        boolean passwordMatched = user.getPassword().equals(rawPassword);
        // 또는 passwordEncoder.matches(rawPassword, user.getEncodedPassword());

        if (!passwordMatched) {
            int failCount = loginFailService.increaseFailCount(userId);
            if (failCount >= 5) {
                return LoginResult.locked("5회 이상 실패로 오늘은 로그인이 잠겼습니다.");
            }
            return LoginResult.fail("아이디 또는 비밀번호가 올바르지 않습니다. (실패 " + failCount + "회)");
        }

        // 4. 성공 시 실패카운트 리셋
        loginFailService.resetFailCount(userId);

        // 토큰 생성이나 세션 처리...
        return LoginResult.success("로그인 성공");
    }
}
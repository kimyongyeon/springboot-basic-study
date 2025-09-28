package kyyspring.springbasicstudy.biz.login.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import kyyspring.springbasicstudy.biz.comm.response.ApiResponse;
import kyyspring.springbasicstudy.biz.login.domain.User;
import kyyspring.springbasicstudy.biz.login.domain.UserType;
import kyyspring.springbasicstudy.biz.login.request.LoginRequest;
import kyyspring.springbasicstudy.biz.login.request.RegisterRequest;
import kyyspring.springbasicstudy.biz.login.response.GuestLoginResponse;
import kyyspring.springbasicstudy.biz.login.response.JwtResponse;
import kyyspring.springbasicstudy.biz.login.response.SessionResponse;
import kyyspring.springbasicstudy.biz.login.service.UserService;
import kyyspring.springbasicstudy.sys.security.GuestSession;
import kyyspring.springbasicstudy.sys.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    private final UserService userService;

    private final RedisTemplate<String, Object> redisTemplate;

    // 회원 가입
    @PostMapping("/member/register")
    public ResponseEntity<?> registerMember(@RequestBody @Valid RegisterRequest request) {
        try {
            User user = userService.registerMember(
                    request.getEmail(),
                    request.getPassword(),
                    request.getName()
            );

            return ResponseEntity.ok(ApiResponse.success ("회원가입이 완료되었습니다", true));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), false));
        }
    }

    // 회원 로그인 - JWT 발급
    @PostMapping("/member/login")
    public ResponseEntity<?> memberLogin(@RequestBody @Valid LoginRequest request) {
        try {
            User user = userService.authenticateUser(request.getEmail(), request.getPassword());

            if (user.isMember()) {
                String token = jwtTokenProvider.generateToken(user.getEmail(), UserType.MEMBER);
                return ResponseEntity.ok(new JwtResponse(token, UserType.MEMBER.name()));
            }

            return ResponseEntity.badRequest()
                    .body(ApiResponse.success("회원 계정이 아닙니다", false));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("로그인에 실패했습니다", false));
        }
    }

    // 비회원 세션 생성
    @PostMapping("/guestSession/login")
    public ResponseEntity<?> guestLogin(HttpServletRequest request) {
        HttpSession session = request.getSession(true);
        String guestId = "guest_" + UUID.randomUUID().toString();

        // 비회원 사용자 DB에 임시 저장 (선택적)
        User guestUser = userService.createGuestUser(guestId);

        session.setAttribute("guestId", guestId);
        session.setAttribute("userId", guestUser.getId());
        session.setAttribute("userType", UserType.GUEST.name());
        session.setMaxInactiveInterval(30 * 60); // 30분

        return ResponseEntity.ok(new SessionResponse(guestId, "guest", session.getId()));
    }

    @PostMapping("/guest/login")
    public ResponseEntity<?> guestLogin() {
        String guestId = "guest_" + UUID.randomUUID().toString();

        // ✅ 방법 1: JWT 토큰 발급 (Stateless)
        User guestUser = userService.createGuestUser(guestId);
        String token = jwtTokenProvider.generateToken(guestUser.getEmail(), UserType.GUEST);

        // ✅ 방법 2: Redis에 추가 정보 저장 (장바구니 등)
        String sessionKey = "guest_session:" + guestId;
        GuestSession guestSession = new GuestSession(guestId, guestUser.getId(), System.currentTimeMillis());
        redisTemplate.opsForValue().set(sessionKey, guestSession, Duration.ofMinutes(30));

        return ResponseEntity.ok(new GuestLoginResponse(token, guestId, sessionKey));
    }
}
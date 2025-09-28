package kyyspring.springbasicstudy.biz.login.controller;

import kyyspring.springbasicstudy.biz.login.domain.User;
import kyyspring.springbasicstudy.biz.login.response.UserProfileResponse;
import kyyspring.springbasicstudy.biz.login.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    // ✅ 회원만 접근 가능
    @GetMapping("/member/profile")
    @PreAuthorize("hasRole('MEMBER')")
    public ResponseEntity<?> getMemberProfile(Authentication auth) {
        String email = auth.getName();
        User user = userService.findByEmail(email);
        return ResponseEntity.ok(new UserProfileResponse(user));
    }

    // ✅ 비회원만 접근 가능
    @GetMapping("/guest/cart")
    @PreAuthorize("hasRole('GUEST')")
    public ResponseEntity<?> getGuestCart(Authentication auth) {
        String guestId = auth.getName();
        return ResponseEntity.ok("비회원 장바구니: " + guestId);
    }

    // ✅ 회원 또는 비회원 모두 접근 가능
    @GetMapping("/common/products")
    @PreAuthorize("hasRole('MEMBER') or hasRole('GUEST')")
    public ResponseEntity<?> getProducts() {
        return ResponseEntity.ok("상품 목록");
    }

    // ✅ 인증된 사용자만 (역할 무관)
    @GetMapping("/auth/info")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getAuthInfo(Authentication auth) {
        return ResponseEntity.ok("인증된 사용자: " + auth.getName());
    }
}

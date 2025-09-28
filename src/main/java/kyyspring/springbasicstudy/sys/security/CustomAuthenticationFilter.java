package kyyspring.springbasicstudy.sys.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kyyspring.springbasicstudy.biz.login.domain.UserType;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        HttpSession session = request.getSession(false);

        Authentication authentication = null;

        // 1. JWT 토큰 확인 (회원)
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtTokenProvider.validateToken(token)) {
                String email = jwtTokenProvider.getEmailFromToken(token);
                UserType userType = jwtTokenProvider.getUserTypeFromToken(token);

                if (userType == UserType.MEMBER) {
                    authentication = createAuthenticationToken(email, userType);
                }
            }
        }
        // 2. 세션 확인 (비회원)
        else if (session != null) {
            String guestId = (String) session.getAttribute("guestId");
            if (guestId != null) {
                authentication = createAuthenticationToken(guestId, UserType.GUEST);
            }
        }

        if (authentication != null) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private Authentication createAuthenticationToken(String identifier, UserType userType) {
        List<SimpleGrantedAuthority> authorities = Arrays.asList(
                new SimpleGrantedAuthority("ROLE_" + userType.name())
        );
        return new UsernamePasswordAuthenticationToken(identifier, null, authorities);
    }
}
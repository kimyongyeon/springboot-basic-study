package kyyspring.springbasicstudy.sys.security;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.HashMap;
import java.util.Map;

// 비회원 세션 데이터 클래스
@JsonSerialize
public class GuestSession {
    private String guestId;
    private Long userId;
    private Long createdAt;
    private Map<String, Object> data = new HashMap<>();

    // 생성자, getter, setter
    public GuestSession(String guestId, Long userId, Long createdAt) {
        this.guestId = guestId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // 장바구니 등 추가 데이터 저장용
    public void setCartData(Object cartData) {
        this.data.put("cart", cartData);
    }
}
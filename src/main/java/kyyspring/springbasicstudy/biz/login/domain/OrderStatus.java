package kyyspring.springbasicstudy.biz.login.domain;

public enum OrderStatus {
    PENDING("대기중"),
    CONFIRMED("확인됨"),
    SHIPPED("배송중"),
    DELIVERED("배송완료"),
    CANCELLED("취소됨");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
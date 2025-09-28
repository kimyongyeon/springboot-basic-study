package kyyspring.springbasicstudy.biz.login.request;

// 재고 변경 타입 Enum
public enum StockChangeType {
    ADD("추가"),
    SUBTRACT("차감"),
    SET("설정");

    private final String description;

    StockChangeType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;

import java.math.BigDecimal;
import java.util.List;

public class ProductSearchRequest {

    private String keyword; // 상품명, 설명에서 검색

    private Long categoryId;

    private String categoryName;

    private String brandName;

    @DecimalMin(value = "0", message = "최소 가격은 0 이상이어야 합니다")
    private BigDecimal minPrice;

    @DecimalMin(value = "0", message = "최대 가격은 0 이상이어야 합니다")
    private BigDecimal maxPrice;

    private Boolean inStock; // 재고 있는 상품만

    private Boolean enabled; // 활성화된 상품만

    private String sortBy = "createdAt"; // name, price, createdAt, stockQuantity

    private String sortDirection = "desc"; // asc, desc

    private Integer page = 0;

    @Max(value = 100, message = "페이지 크기는 100을 초과할 수 없습니다")
    private Integer size = 20;

    private List<String> tags;

    private String color;

    private String productSize;

    // 기본 생성자
    public ProductSearchRequest() {}

    // 간단한 검색용 생성자
    public ProductSearchRequest(String keyword) {
        this.keyword = keyword;
    }

    // 카테고리 검색용 생성자
    public ProductSearchRequest(String keyword, Long categoryId, String brandName) {
        this.keyword = keyword;
        this.categoryId = categoryId;
        this.brandName = brandName;
    }

    // 유효성 검증 메서드
    @AssertTrue(message = "최대 가격은 최소 가격보다 커야 합니다")
    private boolean isPriceRangeValid() {
        if (minPrice != null && maxPrice != null) {
            return maxPrice.compareTo(minPrice) >= 0;
        }
        return true;
    }

    // Getter, Setter
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public BigDecimal getMinPrice() { return minPrice; }
    public void setMinPrice(BigDecimal minPrice) { this.minPrice = minPrice; }

    public BigDecimal getMaxPrice() { return maxPrice; }
    public void setMaxPrice(BigDecimal maxPrice) { this.maxPrice = maxPrice; }

    public Boolean getInStock() { return inStock; }
    public void setInStock(Boolean inStock) { this.inStock = inStock; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortDirection() { return sortDirection; }
    public void setSortDirection(String sortDirection) { this.sortDirection = sortDirection; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public String getProductSize() { return productSize; }
    public void setProductSize(String size) { this.productSize = size; }
}
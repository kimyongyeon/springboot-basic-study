package kyyspring.springbasicstudy.biz.login.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.hibernate.validator.constraints.URL;

import java.math.BigDecimal;

@Valid
public class ProductRequest {

    @NotBlank(message = "상품명은 필수입니다")
    @Size(min = 2, max = 100, message = "상품명은 2-100자 사이여야 합니다")
    private String name;

    @Size(max = 1000, message = "상품 설명은 1000자를 초과할 수 없습니다")
    private String description;

    @NotNull(message = "가격은 필수입니다")
    @DecimalMin(value = "0.01", message = "가격은 0.01 이상이어야 합니다")
    @DecimalMax(value = "999999.99", message = "가격은 999,999.99를 초과할 수 없습니다")
    private BigDecimal price;

    @NotNull(message = "재고 수량은 필수입니다")
    @Min(value = 0, message = "재고 수량은 0 이상이어야 합니다")
    @Max(value = 99999, message = "재고 수량은 99,999를 초과할 수 없습니다")
    private Integer stockQuantity;

    private Long categoryId;

    @Size(max = 50, message = "카테고리명은 50자를 초과할 수 없습니다")
    private String categoryName;

    @Size(max = 50, message = "브랜드명은 50자를 초과할 수 없습니다")
    private String brandName;

    @Pattern(regexp = "^[A-Z0-9-]{3,20}$", message = "상품코드는 3-20자의 영문 대문자, 숫자, 하이픈만 사용 가능합니다")
    private String productCode;

    @URL(message = "올바른 이미지 URL 형식이 아닙니다")
    private String imageUrl;

    private Boolean enabled = true;

    // 기본 생성자
    public ProductRequest() {}

    // 필수 필드 생성자
    public ProductRequest(String name, BigDecimal price, Integer stockQuantity) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // 전체 생성자
    public ProductRequest(String name, String description, BigDecimal price, Integer stockQuantity,
                          String categoryName, String brandName, String productCode) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.categoryName = categoryName;
        this.brandName = brandName;
        this.productCode = productCode;
    }

    // Getter, Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    public String getBrandName() { return brandName; }
    public void setBrandName(String brandName) { this.brandName = brandName; }

    public String getProductCode() { return productCode; }
    public void setProductCode(String productCode) { this.productCode = productCode; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    @Override
    public String toString() {
        return "ProductRequest{" +
                "name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockQuantity=" + stockQuantity +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", brandName='" + brandName + '\'' +
                ", productCode='" + productCode + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", enabled=" + enabled +
                '}';
    }
}

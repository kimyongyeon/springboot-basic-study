package kyyspring.springbasicstudy.biz.login.service;

import kyyspring.springbasicstudy.biz.login.domain.Product;
import kyyspring.springbasicstudy.biz.login.request.*;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface ProductService {

    // 기본 CRUD
    Product createProduct(CreateProductRequest request);
    Product findById(Long productId);
    Product findByProductCode(String productCode);
    Product updateProduct(Long productId, UpdateProductRequest request);
    void deleteProduct(Long productId);

    // 검색 및 조회
    Page<Product> searchProducts(ProductSearchRequest request);
    List<Product> findByCategory(Long categoryId);
    List<Product> findByBrand(String brandName);
    List<Product> findByKeyword(String keyword);

    // 재고 관리
    Product updateStock(UpdateStockRequest request);
    Product addStock(Long productId, Integer quantity, String reason);
    Product subtractStock(Long productId, Integer quantity, String reason);
    Product setStock(Long productId, Integer quantity, String reason);
    List<Product> findLowStockProducts(Integer threshold);

    // 대량 작업
    List<Product> bulkUpdate(BulkProductUpdateRequest request);
    void bulkUpdateEnabled(List<Long> productIds, Boolean enabled, String reason);
    void bulkUpdateCategory(List<Long> productIds, Long categoryId, String categoryName, String reason);
    void bulkUpdatePrice(List<Long> productIds, BigDecimal adjustment, String adjustmentType, String reason);

    // 가격 관리
    Product updatePrice(Long productId, BigDecimal newPrice);
    List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice);

    // 상태 관리
    Product enableProduct(Long productId);
    Product disableProduct(Long productId);

    // 통계 및 분석
    Long getTotalProductCount();
    Long getActiveProductCount();
    Long getOutOfStockCount();
    Map<String, Long> getProductCountByCategory();
    BigDecimal getAveragePrice();

    // 유효성 검증
    boolean existsByProductCode(String productCode);
    boolean isStockAvailable(Long productId, Integer requiredQuantity);
}
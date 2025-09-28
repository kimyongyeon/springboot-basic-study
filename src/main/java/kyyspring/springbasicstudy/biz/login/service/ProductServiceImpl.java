package kyyspring.springbasicstudy.biz.login.service;

import jakarta.persistence.EntityNotFoundException;
import kyyspring.springbasicstudy.biz.login.domain.Product;
import kyyspring.springbasicstudy.biz.login.repository.ProductRepository;
import kyyspring.springbasicstudy.biz.login.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product createProduct(CreateProductRequest request) {
        try {
            // 상품 코드 중복 확인
            if (request.getProductCode() != null && existsByProductCode(request.getProductCode())) {
                throw new IllegalArgumentException("이미 존재하는 상품 코드입니다: " + request.getProductCode());
            }

            Product product = new Product();
            product.setName(request.getName());
            product.setDescription(request.getDescription());
            product.setPrice(request.getPrice());
            product.setStockQuantity(request.getStockQuantity());
            product.setCategoryId(request.getCategoryId());
            product.setCategoryName(request.getCategoryName());
            product.setBrandName(request.getBrandName());
            product.setProductCode(request.getProductCode());
            product.setImageUrl(request.getImageUrl());
            product.setEnabled(request.getEnabled());
            product.setManufacturer(request.getManufacturer());
            product.setCountryOfOrigin(request.getCountryOfOrigin());
            product.setWeight(request.getWeight());
            product.setColor(request.getColor());
            product.setSize(request.getSize());
            product.setCreatedAt(LocalDateTime.now());
            product.setUpdatedAt(LocalDateTime.now());

            // 상품 코드 자동 생성 (없는 경우)
            if (product.getProductCode() == null) {
                product.setProductCode(generateProductCode());
            }

            Product savedProduct = productRepository.save(product);
            logger.info("Product created: id={}, name={}, code={}",
                    savedProduct.getId(), savedProduct.getName(), savedProduct.getProductCode());

            return savedProduct;
        } catch (Exception e) {
            logger.error("Failed to create product: {}", request.getName(), e);
            throw new RuntimeException("상품 생성에 실패했습니다", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Product findById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productId));
    }

    @Override
    @Transactional(readOnly = true)
    public Product findByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다: " + productCode));
    }

    @Override
    public Product updateProduct(Long productId, UpdateProductRequest request) {
        Product product = findById(productId);

        if (request.getName() != null) {
            product.setName(request.getName());
        }
        if (request.getDescription() != null) {
            product.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            product.setPrice(request.getPrice());
        }
        if (request.getStockQuantity() != null) {
            product.setStockQuantity(request.getStockQuantity());
        }
        if (request.getCategoryId() != null) {
            product.setCategoryId(request.getCategoryId());
        }
        if (request.getCategoryName() != null) {
            product.setCategoryName(request.getCategoryName());
        }
        if (request.getBrandName() != null) {
            product.setBrandName(request.getBrandName());
        }
        if (request.getImageUrl() != null) {
            product.setImageUrl(request.getImageUrl());
        }
        if (request.getEnabled() != null) {
            product.setEnabled(request.getEnabled());
        }
        if (request.getManufacturer() != null) {
            product.setManufacturer(request.getManufacturer());
        }
        if (request.getCountryOfOrigin() != null) {
            product.setCountryOfOrigin(request.getCountryOfOrigin());
        }
        if (request.getWeight() != null) {
            product.setWeight(request.getWeight());
        }
        if (request.getColor() != null) {
            product.setColor(request.getColor());
        }
        if (request.getSize() != null) {
            product.setSize(request.getSize());
        }

        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        logger.info("Product updated: id={}, name={}", productId, updatedProduct.getName());

        return updatedProduct;
    }

    @Override
    public void deleteProduct(Long productId) {
        Product product = findById(productId);

        // 소프트 삭제 (비활성화)
        product.setEnabled(false);
        product.setUpdatedAt(LocalDateTime.now());
        productRepository.save(product);

        logger.info("Product disabled: id={}, name={}", productId, product.getName());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(ProductSearchRequest request) {
        // 정렬 설정
        Sort sort = createSort(request.getSortBy(), request.getSortDirection());
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), sort);

        return productRepository.searchProducts(
                request.getKeyword(),
                request.getCategoryId(),
                request.getBrandName(),
                request.getMinPrice(),
                request.getMaxPrice(),
                request.getInStock(),
                request.getEnabled(),
                pageable
        );
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByCategory(Long categoryId) {
        return productRepository.findByCategoryIdAndEnabledTrue(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByBrand(String brandName) {
        return productRepository.findByBrandNameAndEnabledTrue(brandName);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByKeyword(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public Product updateStock(UpdateStockRequest request) {
        Product product = findById(request.getProductId());

        switch (request.getChangeType()) {
            case ADD:
                return addStock(request.getProductId(), request.getQuantity(), request.getReason());
            case SUBTRACT:
                return subtractStock(request.getProductId(), request.getQuantity(), request.getReason());
            case SET:
                return setStock(request.getProductId(), request.getQuantity(), request.getReason());
            default:
                throw new IllegalArgumentException("잘못된 재고 변경 타입: " + request.getChangeType());
        }
    }

    @Override
    public Product addStock(Long productId, Integer quantity, String reason) {
        Product product = findById(productId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("추가할 수량은 0보다 커야 합니다");
        }

        int newStock = product.getStockQuantity() + quantity;
        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        logger.info("Stock added: productId={}, added={}, newStock={}, reason={}",
                productId, quantity, newStock, reason);

        return updatedProduct;
    }

    @Override
    public Product subtractStock(Long productId, Integer quantity, String reason) {
        Product product = findById(productId);

        if (quantity <= 0) {
            throw new IllegalArgumentException("차감할 수량은 0보다 커야 합니다");
        }

        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + product.getStockQuantity());
        }

        int newStock = product.getStockQuantity() - quantity;
        product.setStockQuantity(newStock);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        logger.info("Stock subtracted: productId={}, subtracted={}, newStock={}, reason={}",
                productId, quantity, newStock, reason);

        return updatedProduct;
    }

    @Override
    public Product setStock(Long productId, Integer quantity, String reason) {
        Product product = findById(productId);

        if (quantity < 0) {
            throw new IllegalArgumentException("재고 수량은 0 이상이어야 합니다");
        }

        int oldStock = product.getStockQuantity();
        product.setStockQuantity(quantity);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        logger.info("Stock set: productId={}, oldStock={}, newStock={}, reason={}",
                productId, oldStock, quantity, reason);

        return updatedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findLowStockProducts(Integer threshold) {
        return productRepository.findByStockQuantityLessThan(threshold);
    }

    @Override
    public List<Product> bulkUpdate(BulkProductUpdateRequest request) {
        switch (request.getAction().toUpperCase()) {
            case "ENABLE":
                bulkUpdateEnabled(request.getProductIds(), true, request.getReason());
                break;
            case "DISABLE":
                bulkUpdateEnabled(request.getProductIds(), false, request.getReason());
                break;
            case "UPDATE_CATEGORY":
                bulkUpdateCategory(request.getProductIds(), request.getNewCategoryId(),
                        request.getNewCategoryName(), request.getReason());
                break;
            case "UPDATE_PRICE":
                bulkUpdatePrice(request.getProductIds(), request.getPriceAdjustment(),
                        request.getPriceAdjustmentType(), request.getReason());
                break;
            default:
                throw new IllegalArgumentException("지원하지 않는 작업: " + request.getAction());
        }

        return productRepository.findAllById(request.getProductIds());
    }

    @Override
    public void bulkUpdateEnabled(List<Long> productIds, Boolean enabled, String reason) {
        productRepository.bulkUpdateEnabled(productIds, enabled, LocalDateTime.now());
        logger.info("Bulk update enabled: productIds={}, enabled={}, reason={}",
                productIds.size(), enabled, reason);
    }

    @Override
    public void bulkUpdateCategory(List<Long> productIds, Long categoryId, String categoryName, String reason) {
        productRepository.bulkUpdateCategory(productIds, categoryId, categoryName, LocalDateTime.now());
        logger.info("Bulk update category: productIds={}, categoryId={}, reason={}",
                productIds.size(), categoryId, reason);
    }

    @Override
    public void bulkUpdatePrice(List<Long> productIds, BigDecimal adjustment, String adjustmentType, String reason) {
        List<Product> products = productRepository.findAllById(productIds);

        for (Product product : products) {
            BigDecimal newPrice;
            if ("PERCENT".equals(adjustmentType)) {
                // 퍼센트 조정
                BigDecimal multiplier = adjustment.divide(BigDecimal.valueOf(100)).add(BigDecimal.ONE);
                newPrice = product.getPrice().multiply(multiplier);
            } else {
                // 고정값 조정
                newPrice = product.getPrice().add(adjustment);
            }

            if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
                newPrice = BigDecimal.ZERO;
            }

            product.setPrice(newPrice);
            product.setUpdatedAt(LocalDateTime.now());
        }

        productRepository.saveAll(products);
        logger.info("Bulk update price: productIds={}, adjustment={}, type={}, reason={}",
                productIds.size(), adjustment, adjustmentType, reason);
    }

    @Override
    public Product updatePrice(Long productId, BigDecimal newPrice) {
        Product product = findById(productId);

        if (newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("가격은 0 이상이어야 합니다");
        }

        BigDecimal oldPrice = product.getPrice();
        product.setPrice(newPrice);
        product.setUpdatedAt(LocalDateTime.now());

        Product updatedProduct = productRepository.save(product);
        logger.info("Price updated: productId={}, oldPrice={}, newPrice={}",
                productId, oldPrice, newPrice);

        return updatedProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceRange(minPrice, maxPrice);
    }

    @Override
    public Product enableProduct(Long productId) {
        Product product = findById(productId);
        product.setEnabled(true);
        product.setUpdatedAt(LocalDateTime.now());

        Product enabledProduct = productRepository.save(product);
        logger.info("Product enabled: id={}, name={}", productId, product.getName());

        return enabledProduct;
    }

    @Override
    public Product disableProduct(Long productId) {
        Product product = findById(productId);
        product.setEnabled(false);
        product.setUpdatedAt(LocalDateTime.now());

        Product disabledProduct = productRepository.save(product);
        logger.info("Product disabled: id={}, name={}", productId, product.getName());

        return disabledProduct;
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalProductCount() {
        return productRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getActiveProductCount() {
        return productRepository.countActiveProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getOutOfStockCount() {
        return productRepository.countOutOfStockProducts();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getProductCountByCategory() {
        List<Object[]> results = productRepository.getProductCountByCategory();
        Map<String, Long> categoryStats = new HashMap<>();

        for (Object[] result : results) {
            String categoryName = (String) result[0];
            Long count = (Long) result[1];
            categoryStats.put(categoryName, count);
        }

        return categoryStats;
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal getAveragePrice() {
        return productRepository.getAveragePrice();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByProductCode(String productCode) {
        return productRepository.findByProductCode(productCode).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isStockAvailable(Long productId, Integer requiredQuantity) {
        Product product = findById(productId);
        return product.getStockQuantity() >= requiredQuantity && product.getEnabled();
    }

    // ✅ 헬퍼 메서드들
    private Sort createSort(String sortBy, String sortDirection) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ?
                Sort.Direction.ASC : Sort.Direction.DESC;

        switch (sortBy.toLowerCase()) {
            case "name":
                return Sort.by(direction, "name");
            case "price":
                return Sort.by(direction, "price");
            case "stockquantity":
                return Sort.by(direction, "stockQuantity");
            case "createdat":
            default:
                return Sort.by(direction, "createdAt");
        }
    }

    private String generateProductCode() {
        // 현재 시간 기반으로 상품 코드 생성
        String timestamp = String.valueOf(System.currentTimeMillis());
        String random = String.valueOf((int)(Math.random() * 1000));
        return "PRD-" + timestamp.substring(timestamp.length() - 8) + "-" + random;
    }
}
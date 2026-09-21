package com.nexomart.app.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.nexomart.app.dao.ProductDao;
import com.nexomart.app.dto.ProductRequest;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.Product;
import com.nexomart.app.util.ValidationUtil;

public class ProductService {

    private final ProductDao productDao;

    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    public Product createListing(long sellerId, ProductRequest request) throws ValidationException {
        ValidationUtil.requireNonBlank(request.getName(), "Product name");
        ValidationUtil.requireNonBlank(request.getCategory(), "Category");
        ValidationUtil.requirePositive(request.getPrice(), "Price");
        ValidationUtil.requireNonNegative(request.getStockQty(), "Stock quantity");

        Product product = new Product();
        product.setSellerId(sellerId);
        product.setName(request.getName().trim());
        product.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        product.setPrice(request.getPrice());
        product.setStockQty(request.getStockQty());
        product.setCategory(request.getCategory().trim());
        product.setImageUrl(request.getImageUrl() == null ? "" : request.getImageUrl().trim());
        product.setCreatedAt(LocalDateTime.now());

        return productDao.insert(product);
    }

    public List<Product> search(String keyword, String category) {
        return productDao.search(keyword, category);
    }

    public List<Product> findAll() {
        return productDao.findAll();
    }

    public Optional<Product> findById(long id) {
        return productDao.findById(id);
    }

    public Product updateListing(long sellerId, long productId, ProductRequest request)
            throws ValidationException, BusinessRuleException {
        Product existing = productDao.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found"));

        if (existing.getSellerId() != sellerId) {
            throw new BusinessRuleException("You do not have permission to edit this product");
        }

        ValidationUtil.requireNonBlank(request.getName(), "Product name");
        ValidationUtil.requireNonBlank(request.getCategory(), "Category");
        ValidationUtil.requirePositive(request.getPrice(), "Price");
        ValidationUtil.requireNonNegative(request.getStockQty(), "Stock quantity");

        existing.setName(request.getName().trim());
        existing.setDescription(request.getDescription() == null ? "" : request.getDescription().trim());
        existing.setPrice(request.getPrice());
        existing.setStockQty(request.getStockQty());
        existing.setCategory(request.getCategory().trim());
        existing.setImageUrl(request.getImageUrl() == null ? "" : request.getImageUrl().trim());

        productDao.update(existing);
        return existing;
    }

    public void deleteListing(long sellerId, long productId) throws ValidationException, BusinessRuleException {
        Product existing = productDao.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found"));

        if (existing.getSellerId() != sellerId) {
            throw new BusinessRuleException("You do not have permission to delete this product");
        }

        if (productDao.hasExistingOrders(productId)) {
            throw new BusinessRuleException("Cannot delete a product that has existing orders");
        }

        productDao.delete(productId);
    }

    /**
     * Admin moderation: removes a listing regardless of ownership.
     * Still blocked if the product has existing orders, to preserve order history integrity.
     */
    public void adminRemoveListing(long productId) throws ValidationException, BusinessRuleException {
        Product existing = productDao.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found"));

        if (productDao.hasExistingOrders(productId)) {
            throw new BusinessRuleException("Cannot remove a product that has existing orders");
        }

        productDao.delete(productId);
    }
}
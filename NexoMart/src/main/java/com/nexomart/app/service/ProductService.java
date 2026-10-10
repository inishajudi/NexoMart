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

/**
 * Business logic for product listing management.
 */
public class ProductService {

    private final ProductDao productDao;

    /**
     * Constructs a ProductService with the given ProductDao.
     *
     * @param productDao the DAO used for product persistence
     */
    public ProductService(ProductDao productDao) {
        this.productDao = productDao;
    }

    /**
     * Creates a new product listing for the given seller after validating input.
     *
     * @param sellerId the ID of the seller creating the listing
     * @param request  the product details (name, description, price, stock, category, imageUrl)
     * @return the newly created Product
     * @throws ValidationException if any required field is invalid
     */
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

    /**
     * Searches products by keyword and/or category.
     *
     * @param keyword  partial match on product name or description (may be null)
     * @param category exact category filter (may be null)
     * @return list of matching products
     */
    public List<Product> search(String keyword, String category) {
        return productDao.search(keyword, category);
    }

    /**
     * Returns all products in the catalogue.
     *
     * @return list of all products
     */
    public List<Product> findAll() {
        return productDao.findAll();
    }

    /**
     * Finds a single product by its ID.
     *
     * @param id the product ID
     * @return an Optional containing the product, or empty if not found
     */
    public Optional<Product> findById(long id) {
        return productDao.findById(id);
    }

    /**
     * Updates an existing product listing. Only the owning seller may edit.
     *
     * @param sellerId  the ID of the seller making the edit
     * @param productId the ID of the product to update
     * @param request   the updated product details
     * @return the updated Product
     * @throws ValidationException   if the product is not found or input is invalid
     * @throws BusinessRuleException if the seller does not own the listing
     */
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

    /**
     * Deletes a product listing. Only the owning seller may delete,
     * and only if the product has no existing orders.
     *
     * @param sellerId  the ID of the seller requesting deletion
     * @param productId the ID of the product to delete
     * @throws ValidationException   if the product is not found
     * @throws BusinessRuleException if the seller does not own the listing,
     *                               or the product has existing orders
     */
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
     *
     * @param productId the ID of the product to remove
     * @throws ValidationException   if the product is not found
     * @throws BusinessRuleException if the product has existing orders
     */
    public void adminRemoveListing(long productId) throws ValidationException, BusinessRuleException {
        productDao.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found"));

        if (productDao.hasExistingOrders(productId)) {
            throw new BusinessRuleException("Cannot remove a product that has existing orders");
        }

        productDao.delete(productId);
    }
}
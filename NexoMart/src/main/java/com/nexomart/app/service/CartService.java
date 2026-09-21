package com.nexomart.app.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import com.nexomart.app.dao.CartDao;
import com.nexomart.app.dao.ProductDao;
import com.nexomart.app.exception.BusinessRuleException;
import com.nexomart.app.exception.ValidationException;
import com.nexomart.app.model.CartItem;
import com.nexomart.app.model.Product;
import com.nexomart.app.util.ValidationUtil;

public class CartService {

    private final CartDao cartDao;
    private final ProductDao productDao;

    public CartService(CartDao cartDao, ProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    public void addItem(long userId, long productId, int quantity) throws ValidationException, BusinessRuleException {
        ValidationUtil.requirePositiveInt(quantity, "Quantity");

        Product product = productDao.findById(productId)
                .orElseThrow(() -> new ValidationException("Product not found."));

        Optional<CartItem> existing = cartDao.findByUserAndProduct(userId, productId);
        int desiredTotalQty = quantity + existing.map(CartItem::getQuantity).orElse(0);

        if (desiredTotalQty > product.getStockQty()) {
            throw new BusinessRuleException("Only " + product.getStockQty() + " unit(s) of \""
                    + product.getName() + "\" are in stock.");
        }

        if (existing.isPresent()) {
            cartDao.updateQuantity(existing.get().getId(), desiredTotalQty);
        } else {
            CartItem item = new CartItem();
            item.setUserId(userId);
            item.setProductId(productId);
            item.setQuantity(quantity);
            cartDao.insert(item);
        }
    }

    public void updateQuantity(long userId, long cartItemId, int newQuantity)
            throws ValidationException, BusinessRuleException {
        ValidationUtil.requirePositiveInt(newQuantity, "Quantity");

        List<CartItem> items = cartDao.findByUser(userId);
        CartItem target = items.stream()
                .filter(i -> i.getId() == cartItemId)
                .findFirst()
                .orElseThrow(() -> new ValidationException("Cart item not found."));

        if (newQuantity > target.getAvailableStock()) {
            throw new BusinessRuleException("Only " + target.getAvailableStock()
                    + " unit(s) of \"" + target.getProductName() + "\" are in stock.");
        }

        cartDao.updateQuantity(cartItemId, newQuantity);
    }

    public void removeItem(long userId, long cartItemId) throws ValidationException {
        boolean ownsItem = cartDao.findByUser(userId).stream().anyMatch(i -> i.getId() == cartItemId);
        if (!ownsItem) {
            throw new ValidationException("Cart item not found.");
        }
        cartDao.delete(cartItemId);
    }

    public List<CartItem> getCart(long userId) {
        return cartDao.findByUser(userId);
    }

    public BigDecimal getCartTotal(long userId) {
        return cartDao.findByUser(userId).stream()
                .map(CartItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearCart(long userId) {
        cartDao.deleteAllForUser(userId);
    }
}

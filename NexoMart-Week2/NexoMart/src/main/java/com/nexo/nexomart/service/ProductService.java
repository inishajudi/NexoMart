package com.nexo.nexomart.service;

import com.nexo.nexomart.dao.ProductDAO;
import com.nexo.nexomart.dto.ProductDTO;
import com.nexo.nexomart.exception.NotFoundException;
import com.nexo.nexomart.exception.ValidationException;
import com.nexo.nexomart.model.Product;
import com.nexo.nexomart.util.ValidationUtil;

import java.util.List;
import java.util.stream.Collectors;

/** Business rules for browsing/search (F3). Listing create/edit/delete (F2) reuses the
 *  same validation and is wired up the same way — add a SellerProductServlet on top of
 *  this service when you build that screen. */
public class ProductService {

    private final ProductDAO productDAO;

    public ProductService(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    public List<ProductDTO> browse(String keyword, String category) {
        return productDAO.search(keyword, category).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO getById(long id) throws NotFoundException {
        Product p = productDAO.findById(id)
                .orElseThrow(() -> new NotFoundException("Product " + id + " not found"));
        return toDTO(p);
    }

    public ProductDTO create(long sellerId, ProductDTO dto) throws ValidationException {
        ValidationUtil.requireNotBlank(dto.getName(), "name");
        ValidationUtil.requirePositive(dto.getPrice(), "price");
        ValidationUtil.requireNonNegative(dto.getStockQty(), "stockQty");

        Product p = new Product();
        p.setSellerId(sellerId);
        p.setName(dto.getName());
        p.setDescription(dto.getDescription());
        p.setPrice(dto.getPrice());
        p.setStockQty(dto.getStockQty());
        p.setCategory(dto.getCategory());
        p.setImageUrl(dto.getImageUrl());

        return toDTO(productDAO.create(p));
    }

    private ProductDTO toDTO(Product p) {
        ProductDTO dto = new ProductDTO();
        dto.setId(p.getId());
        dto.setName(p.getName());
        dto.setDescription(p.getDescription());
        dto.setPrice(p.getPrice());
        dto.setStockQty(p.getStockQty());
        dto.setCategory(p.getCategory());
        dto.setImageUrl(p.getImageUrl());
        return dto;
    }
}

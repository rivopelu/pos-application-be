package com.pos.app.service.impl;

import com.pos.app.entities.Category;
import com.pos.app.entities.Product;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateProduct;
import com.pos.app.model.response.ResDetailProduct;
import com.pos.app.repositories.CategoryRepository;
import com.pos.app.repositories.ProductRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final CategoryRepository categoryRepository;
    private final AccountService accountService;
    private final ProductRepository productRepository;

    @Override
    public ResponseEnum createNewProduct(ReqCreateProduct req) {
        Optional<Category> category = categoryRepository.findById(req.getCategoryId());
        if (category.isEmpty()) {
            throw new BadRequestException(ResponseEnum.CATEGORY_NOT_FOUND.name());
        }

        Product product = Product.builder()
                .name(req.getName())
                .price(req.getPrice())
                .category(category.get())
                .description(req.getDescription())
                .createdBy(accountService.getCurrentAccountId())
                .clientId(accountService.getCurrentClientId())
                .image(req.getImage())
                .build();


        try {
            productRepository.save(product);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum editProduct(String id, ReqCreateProduct req) {
        Optional<Product> findProduct = productRepository.findById(id);
        Optional<Category> findCategory = categoryRepository.findById(req.getCategoryId());
        if (findProduct.isEmpty()) {
            throw new NotFoundException(ResponseEnum.PRODUCTS_NOT_FOUND.name());
        }
        if (findCategory.isEmpty()) {
            throw new NotFoundException(ResponseEnum.CATEGORY_NOT_FOUND.name());

        }

        try {
            Product product = findProduct.get();
            product.setName(req.getName());
            product.setPrice(req.getPrice());
            product.setDescription(req.getDescription());
            product.setImage(req.getImage());
            product.setCategory(findCategory.get());
            productRepository.save(product);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResDetailProduct getDetailProduct(String id) {

        Optional<Product> findProduct = productRepository.findById(id);
        if (findProduct.isEmpty()) {
            throw new NotFoundException(ResponseEnum.PRODUCTS_NOT_FOUND.name());
        }
        Product product = findProduct.get();


        try {
            return ResDetailProduct.builder()
                    .name(product.getName())
                    .description(product.getDescription())
                    .price(product.getPrice())
                    .id(product.getId())
                    .image(product.getImage())
                    .categoryId(product.getCategory().getId())
                    .categoryName(product.getCategory().getName())
                    .createdBy(accountService.getCurrentAccount(product.getCreatedBy()).getName())
                    .createdDate(product.getCreatedDate())
                    .build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}

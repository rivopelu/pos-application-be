package com.pos.app.service.impl;

import com.pos.app.entities.Category;
import com.pos.app.entities.Product;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateProduct;
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
}

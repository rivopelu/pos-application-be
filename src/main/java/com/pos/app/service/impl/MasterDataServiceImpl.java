package com.pos.app.service.impl;

import com.pos.app.entities.Category;
import com.pos.app.entities.Product;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.response.ResListCategory;
import com.pos.app.model.response.ResListProduct;
import com.pos.app.repositories.CategoryRepository;
import com.pos.app.repositories.ProductRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.MasterDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final CategoryRepository categoryRepository;
    private final AccountService accountService;
    private final ProductRepository productRepository;

    @Override
    public ResponseEnum createCategory(List<ReqCreateCategory> req) {
        int seq = 1;
        boolean checkExistCategory = categoryRepository.existsAllByClientId(accountService.getCurrentClientId());
        if (checkExistCategory) {
            throw new BadRequestException(ResponseEnum.CLIENT_CATEGORY_ALREADY_EXIST.name());
        }
        List<Category> categories = new ArrayList<>();
        for (ReqCreateCategory reqCreateCategory : req) {
            Category.CategoryBuilder builder = Category.builder();
            builder.name(reqCreateCategory.getName());
            builder.createdBy(accountService.getCurrentAccountId());
            builder.clientId(accountService.getCurrentClientId());
            builder.seq(seq);
            Category category = builder
                    .build();
            categories.add(category);
            seq++;

        }

        try {
            categoryRepository.saveAll(categories);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public List<ResListCategory> getAllCategories() {
        List<ResListCategory> resListCategories = new ArrayList<>();
        List<Category> categoryList = categoryRepository.findAllByClientIdOrderBySeqAsc(accountService.getCurrentClientIdOrNull());
        for (Category category : categoryList) {
            ResListCategory resListCategory = new ResListCategory();
            resListCategory.setName(category.getName());
            resListCategory.setId(category.getId());
            resListCategories.add(resListCategory);
        }
        try {
            return resListCategories;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public List<ResListProduct> getAllProduct() {
        List<ResListProduct> resListProducts = new ArrayList<>();
        List<Product> products = productRepository.findAllByClientId(accountService.getCurrentClientIdOrNull());
        for (Product product : products) {
            ResListProduct resListProduct = ResListProduct.builder()
                    .name(product.getName())
                    .id(product.getId())
                    .price(product.getPrice())
                    .image(product.getImage())
                    .description(product.getDescription())
                    .categoryName(product.getCategory().getName())
                    .categoryId(product.getCategory().getId())
                    .build();
            resListProducts.add(resListProduct);
        }
        try {
            return resListProducts;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }

    }
}

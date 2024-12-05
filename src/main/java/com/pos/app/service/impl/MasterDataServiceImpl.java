package com.pos.app.service.impl;

import com.github.javafaker.Cat;
import com.pos.app.entities.Category;
import com.pos.app.entities.Product;
import com.pos.app.entities.QrCode;
import com.pos.app.entities.SubscriptionPackage;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.request.ReqCreateSubscriptionPackage;
import com.pos.app.model.response.ResListCategory;
import com.pos.app.model.response.ResListProduct;
import com.pos.app.model.response.ResponseListAccount;
import com.pos.app.repositories.CategoryRepository;
import com.pos.app.repositories.ProductRepository;
import com.pos.app.repositories.QrCodeRepository;
import com.pos.app.repositories.SubscriptionPackageRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.MasterDataService;
import com.pos.app.utils.EntityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MasterDataServiceImpl implements MasterDataService {

    private final CategoryRepository categoryRepository;
    private final AccountService accountService;
    private final ProductRepository productRepository;
    private final QrCodeRepository qrCodeRepository;
    private final SubscriptionPackageRepository subscriptionPackageRepository;

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
        return buildResProduct(resListProducts, products);

    }

    @Override
    public List<ResListProduct> getPublicAllProduct(String code) {
        Optional<QrCode> findQrCode = qrCodeRepository.findByCode(code);
        if (findQrCode.isEmpty()) {
            throw new BadRequestException(ResponseEnum.REQUEST_INVALID.name());
        }
        QrCode qrCode = findQrCode.get();
        List<ResListProduct> resListProducts = new ArrayList<>();
        String clientId = qrCode.getClient().getId();
        List<Product> products = productRepository.findAllByClientId(clientId);
        return buildResProduct(resListProducts, products);
    }

    @Override
    public List<ResListCategory> getListCategoryPublic(String code) {
        List<ResListCategory> response = new ArrayList<>();
        Optional<QrCode> qrCode = qrCodeRepository.findByCode(code);
        if (qrCode.isEmpty()) {
            throw new BadRequestException(ResponseEnum.REQUEST_INVALID.name());
        }
        List<Category> categoryList = categoryRepository.findAllByClientIdOrderBySeqAsc(qrCode.get().getClient().getId());
        try {
            for (Category category : categoryList) {
                ResListCategory resListCategory = new ResListCategory();
                resListCategory.setName(category.getName());
                resListCategory.setId(category.getId());
                response.add(resListCategory);
            }
            return response;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum createSubscriptionPackage(List<ReqCreateSubscriptionPackage> req) {
        String currentAccountId = accountService.getCurrentAccountId();
        List<SubscriptionPackage> subscriptionPackageList = new ArrayList<>();
        for (ReqCreateSubscriptionPackage reqCreateSubscriptionPackage : req) {
            SubscriptionPackage subscriptionPackage = SubscriptionPackage.builder()
                    .name(reqCreateSubscriptionPackage.getPackageName())
                    .price(reqCreateSubscriptionPackage.getPrice())
                    .description(reqCreateSubscriptionPackage.getDescription())
                    .durationPerDay(reqCreateSubscriptionPackage.getDurationPerDay())
                    .build();
            EntityUtils.created(subscriptionPackage, currentAccountId);
            subscriptionPackageList.add(subscriptionPackage);
        }
        try {
            subscriptionPackageRepository.saveAll(subscriptionPackageList);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    private List<ResListProduct> buildResProduct(List<ResListProduct> resListProducts, List<Product> products) {
        for (Product product : products) {
            ResListProduct resListProduct = ResListProduct.builder()
                    .name(product.getName())
                    .id(product.getId())
                    .price(product.getPrice())
                    .createdBy(accountService.getCurrentAccount(product.getCreatedBy()).getName())
                    .createdDate(product.getCreatedDate())
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

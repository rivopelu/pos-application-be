package com.pos.app.service.impl;

import com.pos.app.entities.Category;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.repositories.CategoryRepository;
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
}

package com.pos.app.service;

import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.request.ReqCreateMerchant;
import com.pos.app.model.request.ReqCreateSubscriptionPackage;
import com.pos.app.model.response.ResListCategory;
import com.pos.app.model.response.ResListProduct;
import com.pos.app.model.response.ResponseListSubscriptionPackage;

import java.util.List;

public interface MasterDataService {
    ResponseEnum createCategory(List<ReqCreateCategory> req);

    List<ResListCategory> getAllCategories();

    List<ResListProduct> getAllProduct();

    List<ResListProduct> getPublicAllProduct(String code);

    List<ResListCategory> getListCategoryPublic(String code);

    ResponseEnum createSubscriptionPackage(List<ReqCreateSubscriptionPackage> req);

    List<ResponseListSubscriptionPackage> getListSubscriptionPackagePublic();

    ResponseEnum createMerchant(ReqCreateMerchant req);
}

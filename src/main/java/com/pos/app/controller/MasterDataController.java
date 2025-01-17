package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.annotations.SuperAdminAccess;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.request.ReqCreateMerchant;
import com.pos.app.model.request.ReqCreateSubscriptionPackage;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@BaseController("master-data")
public interface MasterDataController {

    @PostMapping("v1/category/new")
    BaseResponse createCategory(@RequestBody List<ReqCreateCategory> req);

    @GetMapping("v1/category/list")
    BaseResponse getAllCategories();

    @GetMapping("v1/product/list-all")
    BaseResponse getAllProducts();

    @SuperAdminAccess
    @PostMapping("v1/subscription-package/create")
    BaseResponse createSubscriptionPackage(@RequestBody List<ReqCreateSubscriptionPackage> req);

    @PostMapping("v1/merchant/new")
    BaseResponse createMerchant(@RequestBody ReqCreateMerchant req);

    @PutMapping("v1/merchant/edit/{id}")
    BaseResponse editMerchant(@RequestBody ReqCreateMerchant req, @PathVariable String id);

    @GetMapping("v1/merchant/list")
    BaseResponse getListClientMerchant();

    @DeleteMapping("v1/merchant/delete/{id}")
    BaseResponse deleteMerchant(@PathVariable String id);


}

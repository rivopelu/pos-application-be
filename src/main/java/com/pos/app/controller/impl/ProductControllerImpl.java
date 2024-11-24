package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.ProductController;
import com.pos.app.model.request.ReqCreateProduct;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.ProductService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

@BaseControllerImpl
@RequiredArgsConstructor
public class ProductControllerImpl implements ProductController {

    private final ProductService productService;
    @Override
    public BaseResponse createNewProduct(ReqCreateProduct req) {
        return ResponseHelper.createBaseResponse(productService.createNewProduct(req));
    }
}

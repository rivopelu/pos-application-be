package com.pos.app.service;

import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateProduct;
import com.pos.app.model.response.ResDetailProduct;

public interface ProductService {

    ResponseEnum createNewProduct(ReqCreateProduct product);

    ResponseEnum editProduct(String id, ReqCreateProduct req);

    ResDetailProduct getDetailProduct(String id);
}

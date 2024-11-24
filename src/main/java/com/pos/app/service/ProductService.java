package com.pos.app.service;

import com.pos.app.entities.Product;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateProduct;

public interface ProductService {

    ResponseEnum createNewProduct(ReqCreateProduct product);
}

package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateProduct;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@BaseController("product")
public interface ProductController {

    @PostMapping("v1/new")
    BaseResponse createNewProduct(@RequestBody ReqCreateProduct req);
}

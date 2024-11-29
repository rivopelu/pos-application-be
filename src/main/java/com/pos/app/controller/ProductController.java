package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateProduct;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.*;

@BaseController("product")
public interface ProductController {

    @PostMapping("v1/new")
    BaseResponse createNewProduct(@RequestBody ReqCreateProduct req);

    @PutMapping("v1/edit/{id}")
    BaseResponse editProduct(@PathVariable String id, @RequestBody ReqCreateProduct req);

    @GetMapping("v1/detail/{id}")
    BaseResponse getDetailProduct(@PathVariable String id);


}

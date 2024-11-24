package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@BaseController("master-data")
public interface MasterDataController {

    @PostMapping("v1/category/new")
    BaseResponse createCategory(@RequestBody List<ReqCreateCategory> req);

    @GetMapping("v1/category/list")
    BaseResponse getAllCategories();
}

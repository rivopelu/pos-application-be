package com.pos.app.controller;

import com.pos.app.annotations.BaseController;
import com.pos.app.model.response.BaseResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@BaseController("public")
public interface PublicController {

    @GetMapping("v1/product/list/{code}")
    BaseResponse getProductPublic(@PathVariable("code") String code);
}

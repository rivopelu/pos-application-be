package com.pos.app.service;

import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.response.ResListCategory;

import java.util.List;

public interface MasterDataService {
    ResponseEnum createCategory(List<ReqCreateCategory> req);

    List<ResListCategory> getAllCategories();
}

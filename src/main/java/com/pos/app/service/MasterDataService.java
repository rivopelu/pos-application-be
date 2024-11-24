package com.pos.app.service;

import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateCategory;

import java.util.List;

public interface MasterDataService {
    ResponseEnum createCategory(List<ReqCreateCategory> req);
}

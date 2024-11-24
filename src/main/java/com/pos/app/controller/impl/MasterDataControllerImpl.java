package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.MasterDataController;
import com.pos.app.model.request.ReqCreateCategory;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.MasterDataService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

import java.util.List;

@BaseControllerImpl
@RequiredArgsConstructor
public class MasterDataControllerImpl implements MasterDataController {

    private final MasterDataService masterDataService;

    @Override
    public BaseResponse createCategory(List<ReqCreateCategory> req) {
        return ResponseHelper.createBaseResponse(masterDataService.createCategory(req));
    }
}
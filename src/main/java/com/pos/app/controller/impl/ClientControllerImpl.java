package com.pos.app.controller.impl;

import com.pos.app.annotations.BaseControllerImpl;
import com.pos.app.controller.ClientController;
import com.pos.app.model.request.ReqCreateClient;
import com.pos.app.model.response.BaseResponse;
import com.pos.app.service.ClientService;
import com.pos.app.utils.ResponseHelper;
import lombok.RequiredArgsConstructor;

@BaseControllerImpl
@RequiredArgsConstructor
public class ClientControllerImpl implements ClientController {

    private final ClientService clientService;

    @Override
    public BaseResponse createClient(ReqCreateClient req) {
        return ResponseHelper.createBaseResponse(clientService.createClient(req));
    }

    @Override
    public BaseResponse createClientByUser(ReqCreateClient req) {
        return ResponseHelper.createBaseResponse(clientService.createClientByUser(req));
    }

    @Override
    public BaseResponse editClient(ReqCreateClient req) {

        return  ResponseHelper.createBaseResponse(clientService.editClient(req));
    }
}

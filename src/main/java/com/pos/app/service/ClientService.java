package com.pos.app.service;

import com.pos.app.enums.ResponseEnum;
import com.pos.app.model.request.ReqCreateClient;

public interface ClientService {
    ResponseEnum createClient(ReqCreateClient req);

    ResponseEnum createClientByUser(ReqCreateClient req);
}

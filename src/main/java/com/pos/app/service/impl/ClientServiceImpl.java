package com.pos.app.service.impl;

import com.pos.app.entities.Client;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateClient;
import com.pos.app.repositories.ClientRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final AccountService accountService;

    @Override
    public ResponseEnum createClient(ReqCreateClient req) {
        try {
            Client client = Client.builder().name(req.getName()).logo(req.getLogo()).note(req.getNote()).createdBy(accountService.getCurrentAccountId()).build();
            clientRepository.save(client);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}

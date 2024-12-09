package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateClient;
import com.pos.app.repositories.AccountRepository;
import com.pos.app.repositories.ClientRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

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

    @Override
    public ResponseEnum createClientByUser(ReqCreateClient req) {
        try {
            Client client = Client.builder().name(req.getName()).logo(req.getLogo()).note(req.getNote()).createdBy(accountService.getCurrentAccountId()).build();
            Client clientSave = clientRepository.save(client);
            Account getAccount = accountService.getCurrentAccount();
            Optional<Account> findAccount = accountRepository.findById(getAccount.getId());
            if (findAccount.isEmpty()) {
                throw new BadRequestException(ResponseEnum.ACCOUNT_NOT_FOUND.name());
            }
            Account account = findAccount.get();
            account.setClient(clientSave);
            account.setUpdatedDate(new Date().getTime());
            accountRepository.save(account);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum editClient(ReqCreateClient req) {
        String clientId = accountService.getCurrentClientIdOrNull();
        Optional<Client> findClient = clientRepository.findById(clientId);
        if (findClient.isEmpty()) {
            throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }
        Client client = findClient.get();
        try {
            client.setName(req.getName());
            client.setLogo(req.getLogo());
            client.setNote(req.getNote());
            clientRepository.save(client);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}

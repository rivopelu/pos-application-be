package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.entities.SubscriptionPackage;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqPaymentSubscription;
import com.pos.app.model.response.ResponsePaymentToken;
import com.pos.app.repositories.SubscriptionPackageRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final AccountService accountService;

    @Override
    public ResponsePaymentToken paymentSubscription(ReqPaymentSubscription subscription) {

        Optional<SubscriptionPackage> findPackage = subscriptionPackageRepository.findById(subscription.getPackageId());

        if (findPackage.isEmpty()) {
            throw new BadRequestException(ResponseEnum.PACKAGE_NOT_FOUND.name());
        }

        Account account = accountService.getCurrentAccount();
        Client client = account.getClient();

        if (client == null) {
            throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }


        try {
            return null;

        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}

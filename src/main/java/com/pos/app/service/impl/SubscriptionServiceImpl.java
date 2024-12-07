package com.pos.app.service.impl;

import com.pos.app.entities.Account;
import com.pos.app.entities.Client;
import com.pos.app.entities.SubscriptionOrder;
import com.pos.app.entities.SubscriptionPackage;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.enums.SubscriptionOrderStatusEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqPaymentObject;
import com.pos.app.model.request.ReqPaymentSubscription;
import com.pos.app.model.response.ResponsePaymentToken;
import com.pos.app.model.response.ResponseSubscriptionList;
import com.pos.app.model.response.SnapPaymentResponse;
import com.pos.app.repositories.SubscriptionOrderRepository;
import com.pos.app.repositories.SubscriptionPackageRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.PaymentService;
import com.pos.app.service.SubscriptionService;
import com.pos.app.utils.EntityUtils;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionPackageRepository subscriptionPackageRepository;
    private final AccountService accountService;
    private final SubscriptionOrderRepository subscriptionOrderRepository;
    private final PaymentService paymentService;

    @Override
    public SnapPaymentResponse paymentSubscription(ReqPaymentSubscription subscription) {

        Optional<SubscriptionPackage> findPackage = subscriptionPackageRepository.findById(subscription.getPackageId());

        if (findPackage.isEmpty()) {
            throw new BadRequestException(ResponseEnum.PACKAGE_NOT_FOUND.name());
        }

        Account account = accountService.getCurrentAccount();
        Client client = account.getClient();

        if (client == null) {
            throw new BadRequestException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }

        SubscriptionOrder buildOrder = SubscriptionOrder.builder().account(account).client(client).subscriptionPackage(findPackage.get()).status(SubscriptionOrderStatusEnum.CREATED).totalTransaction(findPackage.get().getPrice()).build();
        EntityUtils.created(buildOrder, account.getId());
        SubscriptionOrder order = subscriptionOrderRepository.save(buildOrder);
        ReqPaymentObject paymentRequest = ReqPaymentObject.builder().transactionDetail(generateTransactionDetail(order)).customersDetails(generateCustomerDetail(account)).itemsDetail(generateItemsDetail(findPackage.get())).build();

        try {
            return paymentService.createPayment(paymentRequest);
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public Page<ResponseSubscriptionList> getSubscriptionList(Pageable pageable) {
        String clientId = accountService.getCurrentClientIdOrNull();
        Page<SubscriptionOrder> subscriptionOrderPage = subscriptionOrderRepository.findAllByClientIdOrderByCreatedDateDesc(pageable, clientId);
        List<ResponseSubscriptionList> response = new ArrayList<>();
        try {
            for (SubscriptionOrder subscriptionOrder : subscriptionOrderPage.getContent()) {
                ResponseSubscriptionList responseSubscriptionList = ResponseSubscriptionList.builder()
                        .createdDate(subscriptionOrder.getCreatedDate())
                        .id(subscriptionOrder.getId())
                        .status(subscriptionOrder.getStatus())
                        .totalTransaction(subscriptionOrder.getTotalTransaction())
                        .build();
                response.add(responseSubscriptionList);
            }
            return new PageImpl<>(response, pageable, subscriptionOrderPage.getTotalElements());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    private ReqPaymentObject.TransactionDetail generateTransactionDetail(SubscriptionOrder order) {
        return ReqPaymentObject.TransactionDetail.builder().grossAmount(order.getTotalTransaction()).orderId(order.getId()).build();
    }

    private ReqPaymentObject.CustomersDetails generateCustomerDetail(Account account) {
        return ReqPaymentObject.CustomersDetails.builder().firstName(account.getName()).email(account.getEmail()).build();
    }

    private ReqPaymentObject.ItemsDetail generateItemsDetail(SubscriptionPackage subscriptionPackage) {
        return ReqPaymentObject.ItemsDetail.builder().name(subscriptionPackage.getName()).id(subscriptionPackage.getId()).price(subscriptionPackage.getPrice()).quantity(BigInteger.ONE).build();
    }
}

package com.pos.app.service.impl;

import com.pos.app.controller.OrderController;
import com.pos.app.entities.Order;
import com.pos.app.entities.OrderProduct;
import com.pos.app.entities.Product;
import com.pos.app.entities.Transaction;
import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.response.ResListOrder;
import com.pos.app.repositories.OrderProductRepository;
import com.pos.app.repositories.OrderRepository;
import com.pos.app.repositories.ProductRepository;
import com.pos.app.repositories.TransactionRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.OrderService;
import com.pos.app.utils.NumberHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final AccountService accountService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final TransactionRepository transactionRepository;

    @Override
    public ResponseEnum createOrder(ReqCreateOrder req) {
        try {

            int index = 0;
            List<ReqCreateOrder.ListProductCreateOrder> productList = req.getProducts();
            List<String> productIds = new ArrayList<>();
            List<BigInteger> productQty = new ArrayList<>();
            List<OrderProduct> orderProductList = new ArrayList<>();
            BigInteger totalPrice = BigInteger.ZERO;

            for (ReqCreateOrder.ListProductCreateOrder listProductCreateOrder : productList) {
                productIds.add(listProductCreateOrder.getProductId());
                productQty.add(BigInteger.valueOf(listProductCreateOrder.getQty()));
            }
            List<Product> existProduct = productRepository.findExistingIdsByIds(productIds);

            boolean checkProduct = existProduct.size() == productIds.size();

            if (!checkProduct) {
                throw new BadRequestException(ResponseEnum.PRODUCTS_NOT_FOUND.name());
            }


            Order orderBuild = Order.builder()
                    .customerName(req.getCustomerName())
                    .status(OrderStatusEnum.CREATED)
                    .isPayment(req.getIsPayment())
                    .orderCode(String.valueOf(new Date().getTime()))
                    .createdBy(accountService.getCurrentAccountId())
                    .build();

            Order orderSave = orderRepository.saveAndFlush(orderBuild);

            for (Product product : existProduct) {
                BigInteger qty = productQty.get(index);
                index++;
                BigInteger total = product.getPrice().multiply(qty);
                totalPrice = totalPrice.add(total);
                OrderProduct orderProduct = OrderProduct.builder()
                        .qty(qty)
                        .totalPrice(total)
                        .pricePerQty(product.getPrice())
                        .product(product)
                        .order(orderSave)
                        .createdBy(accountService.getCurrentAccountId())
                        .build();
                orderProductList.add(orderProduct);
            }

            orderProductRepository.saveAll(orderProductList);

            BigInteger percentage = NumberHelper.getPercentageTotal(req.getTax(), totalPrice);
            Transaction transaction = Transaction.builder()
                    .order(orderSave)
                    .subTotal(totalPrice)
                    .totalTransaction(percentage.add(totalPrice))
                    .taxPercentage(req.getTax())
                    .createdBy(accountService.getCurrentAccountId())
                    .build();

            transactionRepository.save(transaction);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public Page<ResListOrder> getOrderList(Pageable pageable) {
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.ASC, "createdDate"));
        }

        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<ResListOrder> resListOrders = new ArrayList<>();
        for (Order order : orderPage.getContent()) {


            ResListOrder res = ResListOrder.builder()
                    .id(order.getId())
                    .orderCode(order.getOrderCode())
                    .orderStatus(order.getStatus())
                    .isPayment(order.getIsPayment())
                    .createdDate(order.getCreatedDate())
                    .customerName(order.getCustomerName())
                    .totalItems(getListTotalItem(order.getId()))
                    .totalTransaction(getListTotalTransaction(order.getId()))
                    .build();
            resListOrders.add(res);
        }
        try {
            return new PageImpl<>(resListOrders, orderPage.getPageable(), orderPage.getTotalPages());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    private BigInteger getListTotalTransaction(String orderId) {
        List<Transaction> transactionList = transactionRepository.findAllByOrderId(orderId);
        BigInteger totalTransaction = BigInteger.ZERO;
        for (Transaction transaction : transactionList) {
            totalTransaction = totalTransaction.add(transaction.getTotalTransaction());
        }
        return totalTransaction;
    }

    private BigInteger getListTotalItem(String orderId) {
        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrderId(orderId);
        BigInteger totalItems = BigInteger.ZERO;
        for (OrderProduct orderProduct : orderProductList) {
            totalItems = totalItems.add(orderProduct.getQty());
        }
        return totalItems;
    }
}

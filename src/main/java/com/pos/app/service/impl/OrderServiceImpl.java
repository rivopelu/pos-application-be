package com.pos.app.service.impl;

import com.pos.app.entities.Order;
import com.pos.app.entities.OrderProduct;
import com.pos.app.entities.Product;
import com.pos.app.entities.Transaction;
import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.repositories.OrderProductRepository;
import com.pos.app.repositories.OrderRepository;
import com.pos.app.repositories.ProductRepository;
import com.pos.app.repositories.TransactionRepository;
import com.pos.app.service.AccountService;
import com.pos.app.service.OrderService;
import com.pos.app.utils.NumberHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.*;

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
                    .orderCode(String.valueOf(new Date().getTime()))
                    .createdBy(accountService.getCurrentAccountId())
                    .build();

            Order orderSave = orderRepository.saveAndFlush(orderBuild);

            for (Product product : existProduct) {
                BigInteger qty = productQty.get(index);
                System.out.println(productQty.get(index));
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


            Transaction transaction = Transaction.builder()
                    .order(orderSave)
                    .subTotal(totalPrice)
                    .totalTransaction(NumberHelper.getPercentageTotal(req.getTax(), totalPrice))
                    .taxPercentage(req.getTax())
                    .createdBy(accountService.getCurrentAccountId())
                    .build();

            transactionRepository.save(transaction);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }
}

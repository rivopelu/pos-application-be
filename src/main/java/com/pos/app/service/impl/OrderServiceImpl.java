package com.pos.app.service.impl;

import com.pos.app.entities.Order;
import com.pos.app.entities.OrderProduct;
import com.pos.app.entities.Product;
import com.pos.app.entities.Transaction;
import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.NotFoundException;
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
import com.pos.app.utils.UtilsHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final AccountService accountService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final TransactionRepository transactionRepository;

    //    da0a8b4f-d84c-444d-b793-598bfa32730c
    @Override
    public ResponseEnum createOrder(ReqCreateOrder req) {
        try {
            String clientId = accountService.getCurrentClientId();
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

            BigInteger getLatestCode = orderRepository.findLatestCode(clientId);
            Order orderBuild = Order.builder()
                    .status(OrderStatusEnum.IN_PROGRESS)
                    .isPayment(req.getIsPayment())
                    .orderCode(UtilsHelper.generateOrderCode(getLatestCode))
                    .clientId(clientId)
                    .createdBy(accountService.getCurrentAccountId())
                    .build();

            Order orderSave = orderRepository.saveAndFlush(orderBuild);

            for (ReqCreateOrder.ListProductCreateOrder productCreateOrder : productList) {
                Optional<Product> findProduct = productRepository.findById(productCreateOrder.getProductId());
                if (findProduct.isEmpty()) {
                    throw new NotFoundException(ResponseEnum.PRODUCTS_NOT_FOUND.name());
                }
                Product product = findProduct.get();
                BigInteger qty = productQty.get(index);
                index++;
                BigInteger total = product.getPrice().multiply(qty);

                totalPrice = totalPrice.add(total);

                OrderProduct orderProduct = OrderProduct.builder()
                        .qty(qty)
                        .totalPrice(total)
                        .pricePerQty(product.getPrice())
                        .product(product)
                        .clientId(clientId)
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
                    .clientId(clientId)
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
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by(Sort.Direction.DESC, "createdDate"));
        }

        Page<Order> orderPage = orderRepository.findAll(pageable);
        List<ResListOrder> resListOrders = new ArrayList<>();
        for (Order order : orderPage.getContent()) {


            resListOrders.add(buildOrderList(order));
        }
        try {
            return new PageImpl<>(resListOrders, orderPage.getPageable(), orderPage.getTotalPages());
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum readyToTakeOrder(String id) {
        Optional<Order> findOrder = orderRepository.findById(id);
        if (findOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }
        Order order = findOrder.get();
        if (order.getStatus() != OrderStatusEnum.IN_PROGRESS) {
            throw new BadRequestException(ResponseEnum.ORDER_COMPLETED.name());
        }
        order.setStatus(OrderStatusEnum.READY);
        try {
            orderRepository.save(order);

            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum completeOrder(String id) {
        Optional<Order> findOrder = orderRepository.findById(id);
        if (findOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }
        Order order = findOrder.get();
        order.setStatus(OrderStatusEnum.COMPLETED);
        try {
            orderRepository.save(order);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public List<ResListOrder> getLiveOrderList(OrderStatusEnum status) {
        List<ResListOrder> resListOrders = new ArrayList<>();
        String clientId = accountService.getCurrentClientIdOrNull();
        List<Order> orders = orderRepository.findAllByClientIdAndStatus(clientId, status);
        for (Order order : orders) {
            resListOrders.add(buildOrderList(order));
        }
        try {
            return resListOrders;

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

    private ResListOrder buildOrderList(Order order) {
        return ResListOrder.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .orderStatus(order.getStatus())
                .isPayment(order.getIsPayment())
                .createdDate(order.getCreatedDate())
                .totalItems(getListTotalItem(order.getId()))
                .totalTransaction(getListTotalTransaction(order.getId()))
                .build();
    }
}

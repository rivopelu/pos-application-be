package com.pos.app.service.impl;

import com.pos.app.entities.*;
import com.pos.app.enums.OrderStatusEnum;
import com.pos.app.enums.ResponseEnum;
import com.pos.app.exception.BadRequestException;
import com.pos.app.exception.NotFoundException;
import com.pos.app.exception.SystemErrorException;
import com.pos.app.model.request.ReqCreateOrder;
import com.pos.app.model.request.ReqCreateOrderViaQrCode;
import com.pos.app.model.response.*;
import com.pos.app.repositories.*;
import com.pos.app.service.AccountService;
import com.pos.app.service.OrderService;
import com.pos.app.utils.NumberHelper;
import com.pos.app.utils.UtilsHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final ProductRepository productRepository;
    private final AccountService accountService;
    private final OrderRepository orderRepository;
    private final OrderProductRepository orderProductRepository;
    private final TransactionRepository transactionRepository;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ClientRepository clientRepository;
    private final QrCodeRepository qrCodeRepository;

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
            Order orderBuild = Order.builder().status(OrderStatusEnum.IN_PROGRESS).isPayment(req.getIsPayment()).orderCode(UtilsHelper.generateOrderCode(getLatestCode)).clientId(clientId).createdBy(accountService.getCurrentAccountId()).build();

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

                OrderProduct orderProduct = OrderProduct.builder().qty(qty).totalPrice(total).pricePerQty(product.getPrice()).product(product).clientId(clientId).order(orderSave).createdBy(accountService.getCurrentAccountId()).build();
                orderProductList.add(orderProduct);
            }
            orderProductRepository.saveAll(orderProductList);
            BigInteger percentage = NumberHelper.getPercentageTotal(req.getTax(), totalPrice);
            Transaction transaction = Transaction.builder().order(orderSave).subTotal(totalPrice).totalTransaction(percentage.add(totalPrice)).taxPercentage(req.getTax()).clientId(clientId).createdBy(accountService.getCurrentAccountId()).build();

            transactionRepository.save(transaction);
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public Page<ResListOrder> getOrderList(Pageable pageable) {
        String clientId = accountService.getCurrentClientId();
        Page<Order> orderPage = orderRepository.findByClientIdAndIsActiveTrueOrderByCreatedDateDesc(pageable, clientId);
        List<ResListOrder> resListOrders = new ArrayList<>();
        for (Order order : orderPage.getContent()) {
            resListOrders.add(buildOrderList(order));
        }
        try {
            return new PageImpl<>(resListOrders, orderPage.getPageable(), orderPage.getTotalElements());
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
            simpMessagingTemplate.convertAndSend("/topic/live/order/" + order.getClientId(), order.getOrderCode());
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
        String clientId = accountService.getCurrentClientIdOrNull();

        List<ResListOrder> resListOrders = new ArrayList<>();
        List<Order> orders = orderRepository.findAllByClientIdAndStatusOrderByUpdatedDateAsc(clientId, status);

        for (Order order : orders) {
            resListOrders.add(buildOrderList(order));
        }

        try {
            return resListOrders;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseIdQr generateQROrder() {
        String clientId = accountService.getCurrentClientIdOrNull();
        BigInteger getLatestCode = orderRepository.findLatestCode(clientId);
        Optional<Client> client = clientRepository.findById(clientId);
        String currentAccountId = accountService.getCurrentAccountId();
        if (client.isEmpty()) {
            throw new NotFoundException(ResponseEnum.CLIENT_NOT_FOUND.name());
        }

        try {
            Order order = Order.builder().status(OrderStatusEnum.PENDING).isPayment(false).orderCode(UtilsHelper.generateOrderCode(getLatestCode)).clientId(clientId).createdBy(accountService.getCurrentAccountId()).build();
            order = orderRepository.save(order);
            QrCode qrCode = QrCode.builder().code(UUID.randomUUID().toString()).order(order).client(client.get()).createdBy(currentAccountId).updatedBy(currentAccountId).build();
            qrCode = qrCodeRepository.save(qrCode);
            return ResponseIdQr.builder().id(qrCode.getCode()).build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ResponseEnum createOrderViaQr(String code, ReqCreateOrderViaQrCode req) {

        Optional<QrCode> findQrCode = qrCodeRepository.findByCode(code);

        if (findQrCode.isEmpty()) {
            throw new BadRequestException(ResponseEnum.REQUEST_INVALID.name());
        }


        QrCode qrCode = findQrCode.get();
        String clientId = qrCode.getClient().getId();

        Optional<Order> checkOrder = orderRepository.findById(qrCode.getOrder().getId());
        if (checkOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }

        if (!checkOrder.get().getStatus().equals(OrderStatusEnum.PENDING)) {
            throw new BadRequestException(ResponseEnum.REQUEST_INVALID.name());
        }

        Optional<Order> findOrder = orderRepository.findById(qrCode.getOrder().getId());
        if (findOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }
        Order order = findOrder.get();
        int index = 0;
        List<ReqCreateOrderViaQrCode.ListProductCreateOrder> productList = req.getProducts();
        List<String> productIds = new ArrayList<>();
        List<BigInteger> productQty = new ArrayList<>();
        List<OrderProduct> orderProductList = new ArrayList<>();
        BigInteger totalPrice = BigInteger.ZERO;

        for (ReqCreateOrderViaQrCode.ListProductCreateOrder listProductCreateOrder : productList) {
            productIds.add(listProductCreateOrder.getProductId());
            productQty.add(BigInteger.valueOf(listProductCreateOrder.getQty()));
        }
        List<Product> existProduct = productRepository.findExistingIdsByIds(productIds);

        boolean checkProduct = existProduct.size() == productIds.size();

        if (!checkProduct) {
            throw new BadRequestException(ResponseEnum.PRODUCTS_NOT_FOUND.name());
        }

        order.setStatus(OrderStatusEnum.IN_PROGRESS);
        order.setIsPayment(false);


        Order orderSave = orderRepository.saveAndFlush(order);

        for (ReqCreateOrderViaQrCode.ListProductCreateOrder productCreateOrder : productList) {
            Optional<Product> findProduct = productRepository.findById(productCreateOrder.getProductId());
            if (findProduct.isEmpty()) {
                throw new NotFoundException(ResponseEnum.PRODUCTS_NOT_FOUND.name());
            }
            Product product = findProduct.get();
            BigInteger qty = productQty.get(index);
            index++;
            BigInteger total = product.getPrice().multiply(qty);

            totalPrice = totalPrice.add(total);

            OrderProduct orderProduct = OrderProduct.builder().qty(qty).totalPrice(total).pricePerQty(product.getPrice()).product(product).clientId(clientId).order(orderSave).createdBy(order.getCreatedBy()).build();
            orderProductList.add(orderProduct);
        }

        orderProductRepository.saveAll(orderProductList);

        BigInteger percentage = NumberHelper.getPercentageTotal(req.getTax(), totalPrice);
        Transaction transaction = Transaction.builder().order(orderSave).subTotal(totalPrice).totalTransaction(percentage.add(totalPrice)).taxPercentage(req.getTax()).clientId(clientId).createdBy(order.getCreatedBy()).build();

        transactionRepository.save(transaction);
        try {
            return ResponseEnum.SUCCESS;
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseListOrderPublic getListOrderPublic(String code) {
        Optional<QrCode> findCode = qrCodeRepository.findByCode(code);
        if (findCode.isEmpty()) {
            throw new NotFoundException(ResponseEnum.REQUEST_INVALID.name());
        }
        Optional<Order> findOrder = orderRepository.findById(findCode.get().getOrder().getId());
        if (findOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }
        Order order = findOrder.get();

        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrderId(order.getId());
        List<ResListProduct> products = new ArrayList<>();

        for (OrderProduct orderProduct : orderProductList) {
            Product product = orderProduct.getProduct();
            ResListProduct resProduct = ResListProduct.builder().name(orderProduct.getProduct().getName()).id(orderProduct.getProduct().getId()).image(product.getImage()).price(orderProduct.getPricePerQty()).categoryName(product.getCategory().getName()).categoryId(product.getCategory().getId()).build();
            products.add(resProduct);
        }

        BigInteger totalTransaction = BigInteger.ZERO;
        BigInteger subTotal = BigInteger.ZERO;
        List<Transaction> findTransaction = transactionRepository.findAllByOrderId(order.getId());
        for (Transaction transaction : findTransaction) {
            totalTransaction = totalTransaction.add(transaction.getTotalTransaction());
            subTotal = subTotal.add(transaction.getSubTotal());
        }
        try {
            return ResponseListOrderPublic.builder().orderId(order.getId()).tax(BigInteger.valueOf(11)).total(totalTransaction).products(products).subTotal(subTotal).build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseEnum requestBill(String code) {
        Optional<QrCode> findQrCode = qrCodeRepository.findByCode(code);
        if (findQrCode.isEmpty()) {
            throw new BadRequestException(ResponseEnum.REQUEST_INVALID.name());
        }

        try {
            Order order = findQrCode.get().getOrder();
            order.setStatus(OrderStatusEnum.REQUEST_BILL);
            orderRepository.save(order);
            return ResponseEnum.SUCCESS;

        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResStatusOrder checkStatusOrder(String code) {

        Optional<QrCode> findQrCode = qrCodeRepository.findByCode(code);


        if (findQrCode.isEmpty()) {
            throw new BadRequestException(ResponseEnum.REQUEST_INVALID.name());
        }

        Optional<Order> findOrder = orderRepository.findById(findQrCode.get().getOrder().getId());
        if (findOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }

        try {
            return ResStatusOrder.builder()
                    .status(findOrder.get().getStatus())
                    .build();
        } catch (Exception e) {
            throw new SystemErrorException(e);
        }
    }

    @Override
    public ResponseDetailOrder getDetailOrder(String id) {

        Optional<Order> findOrder = orderRepository.findById(id);

        if (findOrder.isEmpty()) {
            throw new NotFoundException(ResponseEnum.ORDER_NOT_FOUND.name());
        }

        Order order = findOrder.get();
        List<OrderProduct> orderProductList = orderProductRepository.findAllByOrderId(order.getId());
        List<ResListProduct> listProducts = new ArrayList<>();

        for (OrderProduct orderProduct : orderProductList) {
            ResListProduct res = ResListProduct.builder()
                    .name(orderProduct.getProduct().getName())
                    .id(orderProduct.getProduct().getId())
                    .image(orderProduct.getProduct().getImage())
                    .price(orderProduct.getTotalPrice())
                    .qty(orderProduct.getQty())
                    .build();
            listProducts.add(res);
        }


        try {
            return ResponseDetailOrder.builder()
                    .code(order.getOrderCode())
                    .id(order.getId())
                    .status(order.getStatus())
                    .products(listProducts)
                    .build();
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
        return ResListOrder.builder().id(order.getId()).orderCode(order.getOrderCode()).orderStatus(order.getStatus()).isPayment(order.getIsPayment()).createdDate(order.getCreatedDate()).totalItems(getListTotalItem(order.getId())).totalTransaction(getListTotalTransaction(order.getId())).build();
    }
}

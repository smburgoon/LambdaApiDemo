package com.example.orders.service;

import com.example.orders.dao.OrderDao;
import com.example.orders.model.Order;

public class OrderService {
    private final OrderDao dao;

    public OrderService(OrderDao dao) {
        this.dao = dao;
    }

    public Order createOrder(Order order) {
        dao.saveOrder(order);
        return order;
    }

    public Order getOrder(String orderId) {
        return dao.getOrder(orderId);
    }

    public void deleteOrder(String orderId) {
        dao.deleteOrder(orderId);
    }
}
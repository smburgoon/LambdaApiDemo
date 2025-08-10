package com.example.orders.repository;

import com.example.orders.model.Order;
import com.example.orders.dao.OrderDao;

public class OrderRepository {

    private final OrderDao dao;

    public OrderRepository(OrderDao dao) {
        this.dao = dao;
    }

    public Order save(Order order) {
        return dao.put(order);
    }

    public Order findById(String orderId) {
        return dao.get(orderId);
    }
}
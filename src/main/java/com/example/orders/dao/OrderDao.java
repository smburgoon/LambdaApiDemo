package com.example.orders.dao;

import com.example.orders.model.Order;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;

public class OrderDao {

    private final DynamoDBMapper mapper;

    public OrderDao(DynamoDBMapper mapper) {
        this.mapper = mapper;
    }

    public Order put(Order order) {
        mapper.save(order);
        return order;
    }

    public Order get(String orderId) {
        return mapper.load(Order.class, orderId);
    }
}
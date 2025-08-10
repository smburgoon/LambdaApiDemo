package com.example.orders.model;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

@DynamoDBTable(tableName = "Orders")
public class Order {
    private String orderId;
    private String customerName;
    private String status;

    @DynamoDBHashKey(attributeName = "orderId")
    public String getOrderId() { return orderId; }

    @DynamoDBAttribute(attributeName = "customerName")
    public String getCustomerName() { return customerName; }

    @DynamoDBAttribute(attributeName = "status")
    public String getStatus() { return status; }

    // setters omitted for brevity
}
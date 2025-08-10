package com.example.orders.handler;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.orders.model.Order;
import com.example.orders.repository.OrderRepository;
import com.example.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class CreateOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    protected final OrderService orderService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Constructor for testing
    public CreateOrderHandler() {
        this.orderService = createOrderService();
    }

    // Protected factory method for testability
    protected OrderService createOrderService() {
        DynamoDBMapper mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
        OrderRepository repository = new OrderRepository(mapper);
        return new OrderService(repository);
    }


    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(body);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            // Parse request body
            String body = request.getBody();
            Order order = objectMapper.readValue(body, Order.class);

            // Basic validation
            if (order.getOrderId() == null || order.getCustomerName() == null || order.getStatus() == null) {
                return createResponse(400, "Missing required fields: orderId, customerName, status");
            }

            // Create order
            Order savedOrder = orderService.createOrder(order);

            // Compose response
            String responseBody = objectMapper.writeValueAsString(Map.of(
                    "message", "Order created successfully",
                    "order", savedOrder
            ));

            return createResponse(200, responseBody);

        } catch (Exception e) {
            context.getLogger().log("Error creating order: " + e.getMessage());
            return createResponse(500, "Internal server error");
        }
    }
}
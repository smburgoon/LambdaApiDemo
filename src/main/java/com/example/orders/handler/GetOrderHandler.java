package com.example.orders.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import com.example.orders.repository.OrderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;

import java.util.Map;

public class GetOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final ObjectMapper objectMapper = new ObjectMapper();
    protected final OrderService orderService;

    // Default constructor for production
    public GetOrderHandler() {
        this.orderService = createOrderService();
    }

    // Protected factory method for testability
    protected OrderService createOrderService() {
        DynamoDBMapper mapper = new DynamoDBMapper(AmazonDynamoDBClientBuilder.defaultClient());
        OrderRepository repository = new OrderRepository(mapper);
        return new OrderService(repository);
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String orderId = request.getPathParameters() != null ? request.getPathParameters().get("orderId") : null;

            if (orderId == null || orderId.isBlank()) {
                return createResponse(400, "Missing or invalid orderId in path");
            }

            Order order = orderService.getOrderById(orderId);

            if (order == null) {
                return createResponse(404, "Order not found");
            }

            String responseBody = objectMapper.writeValueAsString(Map.of(
                    "message", "Order retrieved successfully",
                    "order", order
            ));

            return createResponse(200, responseBody);

        } catch (Exception e) {
            context.getLogger().log("Error retrieving order: " + e.getMessage());
            return createResponse(500, "Internal server error");
        }
    }

    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(Map.of("Content-Type", "application/json"))
                .withBody(body);
    }
}
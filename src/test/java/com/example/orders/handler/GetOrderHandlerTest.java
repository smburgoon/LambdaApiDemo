package com.example.orders.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.example.orders.model.Order;
import com.example.orders.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GetOrderHandlerTest {

    private GetOrderHandler handler;
    private OrderService mockOrderService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockOrderService = mock(OrderService.class);
        objectMapper = new ObjectMapper();

        handler = new GetOrderHandler() {
            @Override
            protected OrderService createOrderService() {
                return mockOrderService;
            }
        };
    }

    @Test
    public void testValidOrderRetrieval() throws Exception {
        // Arrange
        Order order = new Order("123", "Alice", "SHIPPED");
        when(mockOrderService.getOrderById("123")).thenReturn(order);

        APIGatewayProxyRequestEvent request = TestEventUtils.withPathParam("orderId", "123");
        Context mockContext = mock(Context.class);

        // Act
        var response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Order retrieved successfully"));
        assertTrue(response.getBody().contains("Alice"));
    }

    @Test
    public void testMissingOrderIdReturns400() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent(); // No path params
        Context mockContext = mock(Context.class);

        var response = handler.handleRequest(request, mockContext);

        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing or invalid orderId"));
    }

    @Test
    public void testOrderNotFoundReturns404() {
        when(mockOrderService.getOrderById("999")).thenReturn(null);

        APIGatewayProxyRequestEvent request = TestEventUtils.withPathParam("orderId", "999");
        Context mockContext = mock(Context.class);

        var response = handler.handleRequest(request, mockContext);

        assertEquals(404, response.getStatusCode());
        assertTrue(response.getBody().contains("Order not found"));
    }

    @Test
    public void testInternalErrorReturns500() {
        when(mockOrderService.getOrderById("boom")).thenThrow(new RuntimeException("DB failure"));

        APIGatewayProxyRequestEvent request = TestEventUtils.withPathParam("orderId", "boom");
        Context mockContext = mock(Context.class);

        var response = handler.handleRequest(request, mockContext);

        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Internal server error"));
    }
}
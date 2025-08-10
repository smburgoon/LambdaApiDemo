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

public class CreateOrderHandlerTest {

    private CreateOrderHandler handler;
    private OrderService mockOrderService;
    private ObjectMapper objectMapper;

    @BeforeEach
    public void setup() {
        mockOrderService = mock(OrderService.class);
        objectMapper = new ObjectMapper();

        handler = new CreateOrderHandler() {
            @Override
            protected OrderService createOrderService() {
                return mockOrderService;
            }
        };
    }


    @Test
    public void testValidOrderCreation() throws Exception {
        // Arrange
        Order inputOrder = new Order("123", "Alice", "PENDING");
        when(mockOrderService.createOrder(any(Order.class))).thenReturn(inputOrder);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(objectMapper.writeValueAsString(inputOrder));

        Context mockContext = mock(Context.class);

        // Act
        var response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(200, response.getStatusCode());
        assertTrue(response.getBody().contains("Order created successfully"));
        assertTrue(response.getBody().contains("Alice"));
    }

    @Test
    public void testMissingFieldsReturns400() {
        // Arrange
        String invalidJson = "{\"orderId\":\"123\"}"; // Missing customerName and status

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(invalidJson);

        Context mockContext = mock(Context.class);

        // Act
        var response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(400, response.getStatusCode());
        assertTrue(response.getBody().contains("Missing required fields"));
    }

    @Test
    public void testMalformedJsonReturns500() {
        // Arrange
        String badJson = "{not-valid-json}";

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(badJson);

        Context mockContext = mock(Context.class);

        // Act
        var response = handler.handleRequest(request, mockContext);

        // Assert
        assertEquals(500, response.getStatusCode());
        assertTrue(response.getBody().contains("Internal server error"));
    }
}
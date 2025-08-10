package com.example.orders.util;

import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;

import java.util.Map;

public class TestEventUtils {

    public static APIGatewayProxyRequestEvent withPathParam(String key, String value) {
        return new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of(key, value));
    }

    // You can expand this with query params, headers, etc. as needed
}
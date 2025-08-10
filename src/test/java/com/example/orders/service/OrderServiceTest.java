package com.example.orders.service;

import com.example.orders.model.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void createOrder_shouldReturnSavedOrder() {
        Order order = new Order("123", "Widget", 2);
        when(orderRepository.save(order)).thenReturn(order);

        Order result = orderService.createOrder(order);

        assertThat(result).isEqualTo(order);
        verify(orderRepository).save(order);
    }
}

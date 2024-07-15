package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kikopark.localbackend.modules.order.entities.OrderEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderAndTicketsResponse {
    private OrderDto order;
    private TicketsByOrderResponse[] orderItems;
}

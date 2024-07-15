package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {
    private InstituteTicketCartItemRequest[] tickets;
    private int sum;
}

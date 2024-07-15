package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateOrderStatusRequest {
    private UUID orderId;
    private Integer statusId;
}

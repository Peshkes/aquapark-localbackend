package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;


@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TicketsByOrderResponse {
    private String ticketType;
    private UUID orderItemId;
    private UUID institutionTicketsId;
    private Integer count;
}

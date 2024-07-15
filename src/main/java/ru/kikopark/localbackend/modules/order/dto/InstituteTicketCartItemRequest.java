package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@NoArgsConstructor
@Getter
@AllArgsConstructor
public class InstituteTicketCartItemRequest {
    UUID institutionTicketId;
    Integer count;
}

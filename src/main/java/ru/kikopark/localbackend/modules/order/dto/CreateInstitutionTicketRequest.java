package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateInstitutionTicketRequest {
    private Integer ticketId;
    private Integer price;
    private Double extraValue;
    private Integer extraInterval;
}

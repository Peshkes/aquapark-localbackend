package ru.kikopark.localbackend.modules.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateCouponRequest {
    private String code;
    private Integer discountAmount;
    private Timestamp expirationDate;
    private String description;
}

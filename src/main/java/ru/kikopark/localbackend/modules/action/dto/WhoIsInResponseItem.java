package ru.kikopark.localbackend.modules.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class WhoIsInResponseItem {
    private String ticketType;
    private Integer timeLeft;
    private Integer extra;
}

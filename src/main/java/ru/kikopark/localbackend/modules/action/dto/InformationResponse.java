package ru.kikopark.localbackend.modules.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InformationResponse {
    private String ticketType;
    private Action[] actions;
    private Integer timeLeft;
    private Integer extra;
}

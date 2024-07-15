package ru.kikopark.localbackend.modules.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreateClientRequest {
    private UUID orderItemId;
    private String nfcTag;
    private UUID employeeId;
}

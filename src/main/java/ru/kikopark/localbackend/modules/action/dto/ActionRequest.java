package ru.kikopark.localbackend.modules.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class ActionRequest {
    private String nfcTag;
    private UUID employeeId;
}

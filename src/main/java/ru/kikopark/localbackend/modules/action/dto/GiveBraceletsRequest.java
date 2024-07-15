package ru.kikopark.localbackend.modules.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GiveBraceletsRequest {
    private String[] nfcTags;
    private UUID orderItemId;
    private UUID employeeId;
}

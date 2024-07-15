package ru.kikopark.localbackend.modules.base.dto;

import lombok.Getter;

@Getter
public class GetCsrfResponse {
    private String parameterName;
    private String headerName;
    private String token;
}

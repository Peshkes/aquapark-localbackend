package ru.kikopark.localbackend.modules.base.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponseFromRemote {
    private String accessToken;
    private String refreshToken;
    private String[] roles;
    private UUID employeeId;
}

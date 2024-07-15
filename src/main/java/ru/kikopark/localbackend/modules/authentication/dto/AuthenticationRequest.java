package ru.kikopark.localbackend.modules.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AuthenticationRequest {
    private String username;
    private String password;

}

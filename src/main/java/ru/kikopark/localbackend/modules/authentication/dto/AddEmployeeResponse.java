package ru.kikopark.localbackend.modules.authentication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AddEmployeeResponse {
    private String name;
    private String roleName;
}

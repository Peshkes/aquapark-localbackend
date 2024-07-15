package ru.kikopark.localbackend.modules.action.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Action {
    private UUID actionId;
    private Integer actionTypeId;
    private Timestamp time;
    private UUID employeeId;
}
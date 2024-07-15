package ru.kikopark.localbackend.modules.action.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@NoArgsConstructor
public class DeptRequest extends ActionRequest{
    private Integer value;

    public DeptRequest(String nfcTag, UUID employeeId, Integer value) {
        super(nfcTag, employeeId);
        this.value = value;
    }
}

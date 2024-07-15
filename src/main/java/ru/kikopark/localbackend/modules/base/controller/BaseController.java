package ru.kikopark.localbackend.modules.base.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import ru.kikopark.localbackend.modules.base.dto.CreateInstitutionTicketToRemote;
import ru.kikopark.localbackend.modules.base.service.BaseService;
import ru.kikopark.localbackend.utils.AppError;

@RestController
@AllArgsConstructor
public class BaseController {

    private final BaseService baseService;

    @GetMapping("/guest/csrf-token")
    public CsrfToken getCsrfToken(HttpServletRequest request) {
        return (CsrfToken) request.getAttribute(CsrfToken.class.getName());
    }

    @GetMapping("/admin/send-institution")
    public ResponseEntity<?> sendInstitution() {
        ResponseEntity<?> response = baseService.sendInstitutionId();
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body(new AppError(response.getStatusCodeValue(), response.getBody().toString()));
        }
    }

    @GetMapping("/admin/configs")
    public ResponseEntity<String> getConfigValue(@RequestParam String configKey) {
        return baseService.getConfigValue(configKey)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/configs")
    public ResponseEntity<?> updateConfigValue(@RequestParam String configKey, @RequestParam String configValue) {
        boolean isUpdated = baseService.updateConfigValue(configKey, configValue);
        if (isUpdated) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(500).body(new AppError(500, "Failed to update config value"));
        }
    }

    @PostMapping("/admin/new-institution-ticket")
    public ResponseEntity<?> addInstitutionTicket(@RequestBody CreateInstitutionTicketToRemote createInstitutionTicketToRemote) {
        ResponseEntity<?> response = baseService.sendNewInstitutionTicket(createInstitutionTicketToRemote);
        if (response.getStatusCode().is2xxSuccessful()) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(response.getStatusCode())
                    .body(new AppError(response.getStatusCodeValue(), response.getBody().toString()));
        }
    }
}

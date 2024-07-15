package ru.kikopark.localbackend.modules.authentication.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;
import ru.kikopark.localbackend.modules.authentication.dto.AuthenticationResponse;
import ru.kikopark.localbackend.modules.authentication.entities.RoleEntity;
import ru.kikopark.localbackend.modules.authentication.service.AuthenticationService;
import ru.kikopark.localbackend.utils.AppError;

import java.util.List;

@AllArgsConstructor
@RestController
public class AuthenticationController {
    AuthenticationService authenticationService;
    AuthenticationManager authenticationManager;

    @GetMapping("/admin/roles")
    public ResponseEntity<List<RoleEntity>> getAllRoles() {
        List<RoleEntity> roles = authenticationService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping("/admin/new-account")
    public ResponseEntity<?> addAccount(HttpEntity<String> httpEntity) {
        Object result = authenticationService.addNewEmployee(httpEntity);
        return (result instanceof AppError) ?
                AppError.process(result) :
                new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/guest/authentication")
    public ResponseEntity<?> createAuthToken(HttpEntity<String> httpEntity) {
        Object result = authenticationService.authenticateUser(httpEntity);
        return (result instanceof AuthenticationResponse authenticationResponse) ?
                new ResponseEntity<>(authenticationResponse, HttpStatus.OK) :
                AppError.process(result);
    }
}

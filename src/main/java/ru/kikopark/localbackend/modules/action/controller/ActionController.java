package ru.kikopark.localbackend.modules.action.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kikopark.localbackend.modules.action.dto.InformationResponse;
import ru.kikopark.localbackend.modules.action.entities.ActionTypeEntity;
import ru.kikopark.localbackend.modules.action.entities.ClientEntity;
import ru.kikopark.localbackend.modules.action.entities.ExtraEntity;
import ru.kikopark.localbackend.modules.action.service.ActionService;
import ru.kikopark.localbackend.utils.AppError;

import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class ActionController {
    private ActionService actionService;

    @GetMapping("/employee/action-types")
    public ResponseEntity<ActionTypeEntity[]> getActionsTypes() {
        return Optional.ofNullable(actionService.getActionsTypes())
                .map(types -> ResponseEntity.ok(types.orElse(new ActionTypeEntity[0])))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/information-about-client")
    public ResponseEntity<InformationResponse> getInfoByClientId(@RequestParam String nfcTag) {
        return Optional.ofNullable(actionService.getInfoByNfcTag(nfcTag))
                .map(response -> response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employee/extra")
    public ResponseEntity<?> getExtra(@RequestParam String nfcTag, @RequestParam Integer minutes) {
        Object result = actionService.calculateExtra(nfcTag, minutes);
        if (result instanceof Integer res)
            return new ResponseEntity<>(res, HttpStatus.OK);
        else
            return AppError.process(result);
    }

    @GetMapping("/employee/whoisin")
    public ResponseEntity<?> getWhoIsIn() {
        return Optional.ofNullable(actionService.getWhoIsIn())
                .map(response -> response.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/admin/new-client")
    public ResponseEntity<?> addClient(HttpEntity<String> httpEntity) {
        Object insertionSuccess = actionService.addNewClient(httpEntity);
        if (insertionSuccess instanceof ClientEntity) {
            UUID clientId = ((ClientEntity) insertionSuccess).getClientId();
            return new ResponseEntity<>(clientId, HttpStatus.OK);
        } else
            return AppError.process(insertionSuccess);
    }

    @PostMapping("/employee/enter")
    public ResponseEntity<?> enter(HttpEntity<String> httpEntity) {
        return processActionRequest(actionService.enter(httpEntity));
    }

    @PostMapping("/employee/exit")
    public ResponseEntity<?> exit(HttpEntity<String> httpEntity) {
        return processActionRequest(actionService.exit(httpEntity));
    }

    @PostMapping("/admin/finish")
    public ResponseEntity<?> finish(HttpEntity<String> httpEntity) {
        return processActionRequest(actionService.finish(httpEntity));
    }

    @PostMapping("/admin/dept")
    public ResponseEntity<?> dept(HttpEntity<String> httpEntity) {
        Object result = actionService.dept(httpEntity);
        return (result instanceof ExtraEntity) ?
                new ResponseEntity<>(((ExtraEntity) result).getValue(), HttpStatus.OK) :
                AppError.process(result);
    }

    @PostMapping("/admin/bracelets")
    public ResponseEntity<?> bracelet(HttpEntity<String> httpEntity) {
        Object result = actionService.addNewClient(httpEntity);
        return (result instanceof ResponseEntity<?> res) ?
                res :
                AppError.process(result);
    }

    //utils
    private ResponseEntity<?> processActionRequest(Object result) {
        return (result instanceof Integer) ?
                new ResponseEntity<>((Integer) result, HttpStatus.OK) :
                AppError.process(result);
    }
}

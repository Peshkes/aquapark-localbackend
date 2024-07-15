package ru.kikopark.localbackend.modules.base.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.kikopark.localbackend.modules.base.dto.AuthenticationResponseFromRemote;
import ru.kikopark.localbackend.modules.base.dto.CreateInstitutionTicketToRemote;
import ru.kikopark.localbackend.modules.base.dto.GetCsrfResponse;
import ru.kikopark.localbackend.modules.base.entities.ConfigEntity;
import ru.kikopark.localbackend.modules.base.entities.InstitutionEntity;
import ru.kikopark.localbackend.modules.base.repositories.ConfigRepository;
import ru.kikopark.localbackend.modules.base.repositories.InstitutionRepository;
import ru.kikopark.localbackend.modules.order.dto.CreateInstitutionTicketRequest;
import ru.kikopark.localbackend.modules.order.entities.InstitutionTicketEntity;
import ru.kikopark.localbackend.modules.order.repositories.InstitutionTicketsRepository;
import ru.kikopark.localbackend.modules.order.repositories.TicketsRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class BaseService {

    private final RestTemplate restTemplate;
    private final ConfigRepository configRepository;
    private final InstitutionRepository institutionRepository;
    private final TicketsRepository ticketsRepository;

    private final String serverUrl;
    private String accessToken;
    private String refreshToken;
    private String csrfToken;

    public BaseService(RestTemplate restTemplate, ConfigRepository configRepository, InstitutionRepository institutionRepository, TicketsRepository ticketsRepository) {
        this.restTemplate = restTemplate;
        this.configRepository = configRepository;
        this.institutionRepository = institutionRepository;
        this.ticketsRepository = ticketsRepository;
        this.serverUrl = getConfigValue("remote-url").orElse("");
        authenticate();
    }

    public void authenticate() {
        ResponseEntity<GetCsrfResponse> csrfResponse = restTemplate.getForEntity(serverUrl + "/guest/csrf-token", GetCsrfResponse.class);
        if (csrfResponse.getStatusCode() == HttpStatus.OK) {
            GetCsrfResponse responseBody = csrfResponse.getBody();
            if (responseBody != null) {
                this.csrfToken = responseBody.getToken();
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-CSRF-TOKEN", csrfToken);

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "yuwuliquobu-1246@notmail.com");
        requestBody.put("password", "4S^BPmEnAU@hy2uofjN295");

        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);
        ResponseEntity<AuthenticationResponseFromRemote> response = restTemplate.postForEntity(serverUrl + "/guest/authentication", request, AuthenticationResponseFromRemote.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            AuthenticationResponseFromRemote responseBody = response.getBody();
            if (responseBody != null) {
                this.accessToken = responseBody.getAccessToken();
                this.refreshToken = responseBody.getRefreshToken();
            }
        }
    }

    public ResponseEntity<?> sendInstitutionId() {
        Optional<InstitutionEntity> institutionEntity = institutionRepository.findTopByOrderByInstitutionIdAsc();
        if (institutionEntity.isPresent()) {
            HttpEntity<InstitutionEntity> request = new HttpEntity<>(institutionEntity.get());
            return sendRequestToRemoteServer(HttpMethod.POST, request, "/new-institution");
        } else
            return new ResponseEntity<>("Failed to get institution", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ResponseEntity<?> sendNewInstitutionTicket(CreateInstitutionTicketToRemote createInstitutionTicketToRemote) {
        HttpEntity<CreateInstitutionTicketToRemote> request = new HttpEntity<>(createInstitutionTicketToRemote);
        return sendRequestToRemoteServer(HttpMethod.POST, request, "/localserver/new-institution-ticket");
    }

    public ResponseEntity<?> getTicketsByOrderFromRemote(UUID id) {
        HttpEntity<UUID> request = new HttpEntity<>(id);
        return sendRequestToRemoteServer(HttpMethod.GET, request, "/localserver/tickets-by-order");
    }

    public ResponseEntity<?> sendRequestToRemoteServer(HttpMethod method, HttpEntity<?> entity, String endpoint) {
        System.err.println("Sending request to remote server with endpoint: {}" + endpoint);
        try {
            HttpHeaders headers = prepareHeaders();
            System.err.println("Sending request to remote server with headers: {}" + headers);
            HttpEntity<?> requestWithHeaders = new HttpEntity<>(entity != null ? entity.getBody() : null, headers);

            ResponseEntity<String> response = restTemplate.exchange(serverUrl + endpoint, method, requestWithHeaders, String.class);
            System.err.println("Received response from remote server: {}" + response);

            if (response.getStatusCode() == HttpStatus.UNAUTHORIZED || response.getStatusCode() == HttpStatus.FORBIDDEN || response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
                if (response.getHeaders().containsKey("Authorization")) {
                    this.accessToken = response.getHeaders().get("Authorization").get(0).replace("Bearer ", "");
                    headers = prepareHeaders();
                    requestWithHeaders = new HttpEntity<>(entity != null ? entity.getBody() : null, headers);
                    ResponseEntity<String> secondResponse = restTemplate.exchange(serverUrl + endpoint, method, requestWithHeaders, String.class);
                    System.err.println("Received second response from remote server: {}"+ secondResponse);
                    return new ResponseEntity<>(secondResponse.getBody(), secondResponse.getStatusCode());
                }
                authenticate();
                headers = prepareHeaders();
                requestWithHeaders = new HttpEntity<>(entity != null ? entity.getBody() : null, headers);
                ResponseEntity<String> lastResponse = restTemplate.exchange(serverUrl + endpoint, method, requestWithHeaders, String.class);
                System.err.println("Received last response from remote server: {}"+ lastResponse);
                return new ResponseEntity<>(lastResponse.getBody(), lastResponse.getStatusCode());
            }

            return new ResponseEntity<>(response.getBody(), response.getStatusCode());
        } catch (Exception e) {
            System.err.println("Failed to connect to remote server: "+ e);
            return new ResponseEntity<>("Failed to connect to remote server: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public Optional<String> getConfigValue(String configKey) {
        return configRepository.findByConfigKey(configKey).map(ConfigEntity::getConfigValue);
    }

    @Transactional
    public boolean updateConfigValue(String configKey, String configValue) {
        Optional<ConfigEntity> configEntityOptional = configRepository.findByConfigKey(configKey);
        ConfigEntity configEntity;
        if (configEntityOptional.isPresent()) {
            configEntity = configEntityOptional.get();
            configEntity.setConfigValue(configValue);
        } else {
            configEntity = new ConfigEntity();
            configEntity.setConfigKey(configKey);
            configEntity.setConfigValue(configValue);
        }
        configRepository.save(configEntity);
        return configRepository.findByConfigKey(configKey)
                .map(entity -> configValue.equals(entity.getConfigValue()))
                .orElse(false);
    }

    private HttpHeaders prepareHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (accessToken != null) {
            headers.add("Authorization", "Bearer " + accessToken);
        }
        if (refreshToken != null) {
            headers.add("Refresh-Token", refreshToken);
        }
        if (csrfToken != null) {
            headers.add("X-CSRF-TOKEN", csrfToken);
        }
        return headers;
    }

    private InstitutionTicketEntity mapToInstitutionTicketEntity(CreateInstitutionTicketRequest req) {
        return new InstitutionTicketEntity(institutionRepository.getInstitutionId(), ticketsRepository.findTicketEntityByTicketId(req.getTicketId()), req.getPrice(), req.getExtraValue(), req.getExtraInterval());
    }
}

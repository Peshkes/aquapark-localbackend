package ru.kikopark.localbackend.modules.action.service;

import jakarta.transaction.Transactional;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.kikopark.localbackend.modules.action.dto.*;
import ru.kikopark.localbackend.modules.action.entities.*;
import ru.kikopark.localbackend.modules.action.repositories.*;
import ru.kikopark.localbackend.modules.authentication.entities.EmployeeEntity;
import ru.kikopark.localbackend.modules.authentication.repositories.EmployeeRepository;
import ru.kikopark.localbackend.modules.order.entities.InstitutionTicketEntity;
import ru.kikopark.localbackend.modules.order.entities.OrderItemEntity;
import ru.kikopark.localbackend.modules.order.entities.TicketEntity;
import ru.kikopark.localbackend.modules.order.repositories.InstitutionTicketsRepository;
import ru.kikopark.localbackend.modules.order.repositories.OrderItemsRepository;
import ru.kikopark.localbackend.modules.order.repositories.TicketsRepository;
import ru.kikopark.localbackend.utils.AppError;
import ru.kikopark.localbackend.utils.Converter;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@CacheConfig(cacheNames = "actionService")
public class ActionService {

    private final ActionTypeRepository actionTypeRepository;
    private final ClientRepository clientRepository;
    private final ActionRepository actionRepository;
    private final TicketsRepository ticketsRepository;
    private final EmployeeRepository employeeRepository;
    private final OrderItemsRepository orderItemsRepository;
    private final BraceletRepository braceletRepository;
    private final InstitutionTicketsRepository institutionTicketsRepository;
    private final ExtraRepository extraRepository;

    private final ActionTypeEntity ACTION_TYPE_CREATED;
    private final ActionTypeEntity ACTION_TYPE_ENTERED;
    private final ActionTypeEntity ACTION_TYPE_EXITED;
    private final ActionTypeEntity ACTION_TYPE_FINISHED;

    public ActionService(ExtraRepository extraRepository, InstitutionTicketsRepository institutionTicketsRepository, ActionTypeRepository actionTypeRepository, ClientRepository clientRepository, ActionRepository actionRepository, TicketsRepository ticketsRepository, EmployeeRepository employeeRepository, OrderItemsRepository orderItemsRepository, BraceletRepository braceletRepository) {
        this.institutionTicketsRepository = institutionTicketsRepository;
        this.actionTypeRepository = actionTypeRepository;
        this.clientRepository = clientRepository;
        this.actionRepository = actionRepository;
        this.ticketsRepository = ticketsRepository;
        this.employeeRepository = employeeRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.braceletRepository = braceletRepository;
        this.extraRepository = extraRepository;

        ACTION_TYPE_CREATED = actionTypeRepository.findActionTypeEntityByActionTypeId(1);
        ACTION_TYPE_ENTERED = actionTypeRepository.findActionTypeEntityByActionTypeId(2);
        ACTION_TYPE_EXITED = actionTypeRepository.findActionTypeEntityByActionTypeId(3);
        ACTION_TYPE_FINISHED = actionTypeRepository.findActionTypeEntityByActionTypeId(4);
    }

    @Cacheable(key = "'actionTypeEntities'")
    public Optional<ActionTypeEntity[]> getActionsTypes() {
        List<ActionTypeEntity> response = actionTypeRepository.findAll();
        return response.isEmpty() ? Optional.empty() : Optional.of(response.toArray(new ActionTypeEntity[0]));
    }

    public Optional<WhoIsInResponseItem[]> getWhoIsIn() {
        List<ClientEntity> clients = clientRepository.findClientsByInPark(true);
        if (clients.isEmpty()) {
            return Optional.empty();
        } else {
            List<WhoIsInResponseItem> responseItems = clients.stream()
                    .map(this::createWhoIsInResponseItem)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();
            return Optional.of(responseItems.toArray(new WhoIsInResponseItem[0]));
        }
    }

    public Optional<InformationResponse> getInfoByNfcTag(String nfcTag) {
        return createInformationResponse(nfcTag);
    }

    public Object calculateExtra(String nfcTag, Integer minutes) {
        BraceletEntity braceletEntity = braceletRepository.findBraceletEntityByNfcTag(nfcTag);
        if (braceletEntity.getIsActive()) {
            ClientEntity clientEntity = clientRepository.findTopByBracelet_NfcTagOrderByDateCreatedDesc(nfcTag);
            if (clientEntity != null) {
                TicketEntity ticketEntity = ticketsRepository.findTicketEntityByClientId(clientEntity.getClientId());
                if (ticketEntity != null) {
                    return calculateExtra(ticketEntity, minutes);
                } else
                    return new AppError(HttpStatus.NOT_FOUND.value(), "Билет не найден");
            } else
                return new AppError(HttpStatus.NOT_FOUND.value(), "Клиент не найден");
        } else
            return new AppError(HttpStatus.NOT_ACCEPTABLE.value(), "Браслет неактивен");
    }

    private int calculateExtra(TicketEntity ticketEntity, int minutes) {
        InstitutionTicketEntity ite = institutionTicketsRepository.findInstitutionTicketEntityByTicket_TicketId(ticketEntity.getTicketId());
        int extra = (int) (ite.getExtraValue() * Math.ceil((double) minutes / ite.getExtraInterval()));
        return Math.max(extra, 0);
    }

    @Transactional
    public Object enter(HttpEntity<String> httpEntity) {
        return performAction(httpEntity, ACTION_TYPE_ENTERED);
    }

    @Transactional
    public Object exit(HttpEntity<String> httpEntity) {
        return performAction(httpEntity, ACTION_TYPE_EXITED);
    }

    @Transactional
    public Object finish(HttpEntity<String> httpEntity) {
        return performAction(httpEntity, ACTION_TYPE_FINISHED);
    }

    @Transactional
    public Object dept(HttpEntity<String> httpEntity) {
        Optional<DeptRequest> request = Converter.jsonToObject(httpEntity.getBody(), DeptRequest.class);
        if (request.isPresent()) {
            DeptRequest req = request.get();
            ClientEntity client = clientRepository.findTopByBracelet_NfcTagOrderByDateCreatedDesc(req.getNfcTag());
            if (client != null) {
                ExtraEntity createdExtraEntity = new ExtraEntity(client.getClientId(), req.getValue());
                ExtraEntity addedExtraEntity;
                try {
                    addedExtraEntity = extraRepository.save(createdExtraEntity);
                } catch (Exception e) {
                    System.err.println("Ошибка добавления доплаты в базу данных: " + e.getMessage());
                    return new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка добавления доплаты в базу данных");
                }
                client.setHaveExtra(true);
                return addedExtraEntity;
            } else
                return new AppError(HttpStatus.NOT_FOUND.value(), "Клиент не найден");
        } else
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неверные данные");
    }

    @Transactional
    public Object addNewClient(HttpEntity<String> httpEntity) {
        Optional<CreateClientRequest> clientRequest = Converter.jsonToObject(httpEntity.getBody(), CreateClientRequest.class);
        if (clientRequest.isPresent()) {
            CreateClientRequest ccr = clientRequest.get();
            String nfcTag = ccr.getNfcTag();
            BraceletEntity be = braceletRepository.findBraceletEntityByNfcTag(nfcTag);
            if (be == null) {
                try {
                    be = braceletRepository.save(new BraceletEntity(nfcTag));
                } catch (Exception E) {
                    return new AppError(HttpStatus.I_AM_A_TEAPOT.value(), "Ошибка добавления нового браслета");
                }
            }
            if (!be.getIsActive()) {
                OrderItemEntity oie = orderItemsRepository.findOrderItemEntityByOrderItemId(ccr.getOrderItemId());
                ClientEntity newClient = new ClientEntity(oie, be);
                ClientEntity addedClient;
                try {
                    addedClient = clientRepository.save(newClient);
                } catch (Exception e) {
                    System.err.println("Ошибка добавления клиента в базу данных: " + e.getMessage());
                    return new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка добавления клиента в базу данных");
                }
                createActionEntity(newClient, ACTION_TYPE_CREATED, ccr.getEmployeeId());
                be.setIsActive(true);
                return addedClient;
            } else
                return new AppError(HttpStatus.BAD_REQUEST.value(), "Браслет уже используется");
        } else
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неверные данные");
    }

    @Transactional
    public Object giveBracelet(HttpEntity<String> httpEntity) {
        Optional<GiveBraceletsRequest> request = Converter.jsonToObject(httpEntity.getBody(), GiveBraceletsRequest.class);
        if (request.isPresent()) {
            GiveBraceletsRequest req = request.get();
            String[] nfcTags = req.getNfcTags();
            try {
                Arrays.stream(nfcTags).forEach(nfc -> {
                    BraceletEntity be = braceletRepository.findBraceletEntityByNfcTag(nfc);
                    if (be == null) {
                        be = new BraceletEntity(nfc);
                        be.setIsActive(true);
                        braceletRepository.save(be);
                    } else {
                        if (be.getIsActive()) {
                            throw new ResponseStatusException(HttpStatus.CONFLICT, "Браслет уже активен");
                        }
                        be.setIsActive(true);
                        braceletRepository.save(be);
                    }
                    ClientEntity client = new ClientEntity(orderItemsRepository.findOrderItemEntityByOrderItemId(req.getOrderItemId()), be);
                    ClientEntity newClient = clientRepository.save(client);
                    createActionEntity(newClient, ACTION_TYPE_CREATED, req.getEmployeeId());
                });
            } catch (ResponseStatusException ex) {
                return new AppError(ex.getStatusCode().value(), ex.getReason());
            }
            return new ResponseEntity<>("Браслеты успешно выданы", HttpStatus.OK);
        } else {
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неверные данные");
        }
    }

    //utils

    private Optional<WhoIsInResponseItem> createWhoIsInResponseItem(ClientEntity client) {
        String nfcTag = client.getBracelet().getNfcTag();
        return createInformationResponse(nfcTag)
                .map(info -> new WhoIsInResponseItem(info.getTicketType(), info.getTimeLeft(), info.getExtra()));
    }

    private Optional<InformationResponse> createInformationResponse(String nfcTag) {
        ClientEntity ce = clientRepository.findTopByBracelet_NfcTagOrderByDateCreatedDesc(nfcTag);
        if (ce != null) {
            List<ActionEntity> ae = actionRepository.findActionEntitiesByClient_ClientId(ce.getClientId());
            if (!ae.isEmpty()) {
                TicketEntity te = ticketsRepository.findTicketEntityByClientId(ce.getClientId());
                String ticketType = te.getType().getName() + " " + Converter.convertMinutesToHours(te.getTime().getMinutes());
                Integer timeLeft = calculateTimeLeft(ce, ae, te);
                int extra = calculateExtra(te, timeLeft);
                Action[] actions = ae.stream().map(this::createActionDTO).toArray(Action[]::new);
                return Optional.of(new InformationResponse(ticketType, actions, timeLeft, extra));
            }
        }
        return Optional.empty();
    }

    private Action createActionDTO(ActionEntity actionEntity) {
        return new Action(actionEntity.getActionId(), actionEntity.getActionType().getActionTypeId(), actionEntity.getTime(), actionEntity.getEmployee().getEmployeeId());
    }

    private Object performAction(HttpEntity<String> httpEntity, ActionTypeEntity actionType) {
        Optional<ActionRequest> actionRequest = Converter.jsonToObject(httpEntity.getBody(), ActionRequest.class);
        if (actionRequest.isPresent()) {
            ActionRequest request = actionRequest.get();
            ClientEntity client = clientRepository.findTopByBracelet_NfcTagOrderByDateCreatedDesc(request.getNfcTag());
            if (client == null) {
                return new AppError(HttpStatus.BAD_REQUEST.value(), "Клиент не найден");
            }

            BraceletEntity braceletEntity = client.getBracelet();
            if (braceletEntity.getIsActive()) {
                boolean isActionCompleted = false;
                String errorMessage = "";

                if (actionType.equals(ACTION_TYPE_ENTERED)) {
                    if (client.isInPark()) {
                        errorMessage = "Клиент еще внутри";
                    } else {
                        client.setInPark(true);
                        isActionCompleted = true;
                    }
                } else if (actionType.equals(ACTION_TYPE_EXITED)) {
                    if (!client.isInPark()) {
                        errorMessage = "Клиент не внутри";
                    } else {
                        client.setInPark(false);
                        isActionCompleted = true;
                    }
                } else if (actionType.equals(ACTION_TYPE_FINISHED)) {
                    if (client.isInPark()) {
                        errorMessage = "Клиент еще внутри";
                    } else {
                        isActionCompleted = true;
                    }
                } else
                    errorMessage = "Неверный тип действия";

                if (isActionCompleted) {
                    return createAction(client, request.getEmployeeId(), actionType, braceletEntity);
                } else
                    return new AppError(HttpStatus.BAD_REQUEST.value(), errorMessage);
            } else
                return new AppError(HttpStatus.BAD_REQUEST.value(), "Браслет не активирован");
        } else
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неправильные данные");
    }

    private Object createAction(ClientEntity client, UUID employeeId, ActionTypeEntity actionType, BraceletEntity braceletEntity) {
        try {
            Integer minutes = calculateTimeLeft(client);
            if (minutes != null) {
                if (actionType.equals(ACTION_TYPE_FINISHED)) {
                    if (minutes < 0)
                        return new AppError(HttpStatus.LOCKED.value(), "Нужно оплатить задолженность за " + Math.abs(minutes) + " мин", minutes);
                    else
                        braceletEntity.setIsActive(false);
                }
                createActionEntity(client, actionType, employeeId);
                return minutes;
            } else
                return new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Невозможно расчитать время");
        } catch (Exception e) {
            System.err.println("Ошибка создания действия: " + e.getMessage());
            return new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка создания действия");
        }
    }

    private void createActionEntity(ClientEntity client, ActionTypeEntity actionType, UUID employeeId) {
        EmployeeEntity employee = employeeRepository.findEmployeeEntityByEmployeeId(employeeId);
        ActionEntity ae = new ActionEntity(client, actionType, employee);
        actionRepository.save(ae);
    }

    private Integer calculateTimeLeft(ClientEntity client) {
        TicketEntity ticket = ticketsRepository.findTicketEntityByClientId(client.getClientId());

        if (ticket != null) {
            List<ActionEntity> actions = actionRepository.findActionEntitiesByClient_ClientId(client.getClientId());
            return calculateTimeLeft(client, actions, ticket);
        } else {
            return null;
        }
    }

    private Integer calculateTimeLeft(ClientEntity client, List<ActionEntity> actions, TicketEntity ticket) {

        List<ActionEntity> enterActions = filterActionsByType(actions, ACTION_TYPE_ENTERED);
        List<ActionEntity> exitActions = filterActionsByType(actions, ACTION_TYPE_EXITED);

        int totalInsideMinutes = calculateTotalMinutesInside(enterActions, exitActions);
        int minutesLeft = ticket.getTime().getMinutes() - totalInsideMinutes;
        if (minutesLeft < 0) {
            if (client.isHaveExtra())
                return 0;
            else
                return minutesLeft;
        } else
            return minutesLeft;
    }


    private int calculateTotalMinutesInside(List<ActionEntity> enterActions, List<ActionEntity> exitActions) {
        int totalInsideMinutes = 0;

        for (int i = 0; i < enterActions.size(); i++) {
            long enterTimeMillis = enterActions.get(i).getTime().getTime();
            if (i < exitActions.size()) {
                long exitTimeMillis = exitActions.get(i).getTime().getTime();
                totalInsideMinutes += convertMillisToMinutes(exitTimeMillis - enterTimeMillis);
            } else {
                totalInsideMinutes += convertMillisToMinutes(new Timestamp(System.currentTimeMillis()).getTime() - enterTimeMillis);
            }
        }
        return totalInsideMinutes;
    }

    private List<ActionEntity> filterActionsByType(List<ActionEntity> actions, ActionTypeEntity actionType) {
        return actions.stream()
                .filter(action -> action.getActionType().equals(actionType))
                .collect(Collectors.toList());
    }

    private int convertMillisToMinutes(long millis) {
        return (int) (millis / (60 * 1000));
    }
}

package ru.kikopark.localbackend.modules.order.service;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.kikopark.localbackend.modules.base.dto.CreateInstitutionTicketToRemote;
import ru.kikopark.localbackend.modules.base.entities.ConfigEntity;
import ru.kikopark.localbackend.modules.base.service.BaseService;
import ru.kikopark.localbackend.modules.order.dto.*;
import ru.kikopark.localbackend.modules.order.entities.*;
import ru.kikopark.localbackend.modules.order.repositories.*;
import ru.kikopark.localbackend.modules.base.repositories.InstitutionRepository;
import ru.kikopark.localbackend.utils.AppError;
import ru.kikopark.localbackend.utils.Converter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class OrderService {

    BaseService baseService;
    OrdersRepository ordersRepository;
    OrderItemsRepository orderItemsRepository;
    StatusesRepository statusesRepository;
    TypesRepository typesRepository;
    CouponsRepository couponsRepository;
    InstitutionTicketsRepository institutionTicketsRepository;
    InstitutionRepository institutionRepository;
    TimeRepository timeRepository;
    TicketsRepository ticketsRepository;

    StatusEntity STATUS_PAID;
    StatusEntity STATUS_USING;
    StatusEntity STATUS_USED;

    public OrderService(BaseService baseService, TicketsRepository ticketsRepository, OrdersRepository ordersRepository, OrderItemsRepository orderItemsRepository, StatusesRepository statusesRepository, TypesRepository typesRepository, CouponsRepository couponsRepository, InstitutionTicketsRepository institutionTicketsRepository, InstitutionRepository institutionRepository, TimeRepository timeRepository) {
        this.ordersRepository = ordersRepository;
        this.orderItemsRepository = orderItemsRepository;
        this.statusesRepository = statusesRepository;
        this.typesRepository = typesRepository;
        this.couponsRepository = couponsRepository;
        this.institutionTicketsRepository = institutionTicketsRepository;
        this.institutionRepository = institutionRepository;
        this.timeRepository = timeRepository;
        this.ticketsRepository = ticketsRepository;
        this.baseService = baseService;
        this.STATUS_PAID = statusesRepository.getStatusEntityByStatusId(1);
        this.STATUS_USING = statusesRepository.getStatusEntityByStatusId(2);
        this.STATUS_USED = statusesRepository.getStatusEntityByStatusId(3);
    }

    public Object getTicketsAndOrderFromRemote(UUID id) {

        try {
            ResponseEntity<?> orderAndTicketsResponse = baseService.sendRequestToRemoteServer(HttpMethod.GET, null, "/localserver/order-and-tickets?id=" + id);

            if (orderAndTicketsResponse.getStatusCode().equals(HttpStatus.OK)) {
                OrderAndTicketsResponse oatResponse = JsonToOrderAndTicketsResponse(orderAndTicketsResponse.getBody());
                if (oatResponse != null) {
                    OrderDto orderDto = oatResponse.getOrder();
                    if (orderDto.getInstitutionId() != null && orderDto.getInstitutionId().equals(institutionRepository.getInstitutionId())) {
                        if (orderDto.getStatus().equals("paid")) {
                            return oatResponse;
                        } else if (orderDto.getStatus().equals("using")) {
                            return new AppError(HttpStatus.LOCKED.value(), "Заказ используется", orderDto.getDateChanged());
                        } else {
                            return new AppError(HttpStatus.LOCKED.value(), "Заказ уже был использован", orderDto.getDateChanged());
                        }
                    } else {
                        return new AppError(HttpStatus.UNAUTHORIZED.value(), "This ticket is for another institution");
                    }
                } else {
                    return new AppError(orderAndTicketsResponse.getStatusCode().value(), "Failed to get tickets and order from remote server");
                }
            } else {
                return new AppError(orderAndTicketsResponse.getStatusCode().value(), "Failed to get tickets and order from remote server");
            }
        } catch (Exception e) {
            return new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal server error occurred");
        }
    }

    private OrderAndTicketsResponse JsonToOrderAndTicketsResponse(Object body) {
        Optional<OrderAndTicketsResponse> oatResponse = Converter.jsonToObject(body.toString(), OrderAndTicketsResponse.class);
        return oatResponse.orElse(null);
    }


    public Object getTicketsByOrder(UUID id) {
        OrderEntity orderEntity = ordersRepository.getOrderEntityByOrderId(id);
        if (orderEntity != null) {
            if (!orderEntity.getStatus().equals(STATUS_USED)) {
                List<OrderItemEntity> orderItemEntities = orderItemsRepository.getOrderItemEntitiesByOrderId(id);
                return orderItemEntities.stream()
                        .map(OrderService::CreateTicketsByOrderResponse)
                        .collect(Collectors.toList());
            } else
                return new AppError(HttpStatus.LOCKED.value(), "Заказ уже был использован", orderEntity.getDateChanged());
        }
        return new AppError(HttpStatus.NOT_FOUND.value(), "Заказ не найден");
    }

    @Transactional
    public Optional<CouponEntity> addNewCoupon(HttpEntity<String> httpEntity) {
        Optional<CouponEntity> addedCoupon = Optional.empty();
        Optional<CreateCouponRequest> couponRequest = Converter.jsonToObject(httpEntity.getBody(), CreateCouponRequest.class);
        if (couponRequest.isPresent()) {
            CouponEntity newCoupon = couponEntityMapper(couponRequest.get());
            CouponEntity returnedCoupon = couponsRepository.save(newCoupon);
            addedCoupon = Optional.of(returnedCoupon);
        }
        return addedCoupon;
    }

    @Transactional
    public Optional<InstitutionTicketEntity> addNewInstitutionTicket(HttpEntity<String> httpEntity) {
        Optional<InstitutionTicketEntity> addedIte = Optional.empty();
        Optional<CreateInstitutionTicketRequest> request = Converter.jsonToObject(httpEntity.getBody(), CreateInstitutionTicketRequest.class);
        if (request.isPresent()) {
            CreateInstitutionTicketRequest req = request.get();
            InstitutionTicketEntity oldIte = institutionTicketsRepository.findInstitutionTicketEntityByTicket_TicketId(req.getTicketId());
            if (oldIte != null)
                oldIte.setIsActive(false);

            InstitutionTicketEntity ite = institutionTicketEntityMapper(req);
            CreateInstitutionTicketToRemote createInstitutionTicketToRemote = new CreateInstitutionTicketToRemote(ite);

            ResponseEntity<?> success = baseService.sendNewInstitutionTicket(createInstitutionTicketToRemote);

            if (success.getStatusCode() == HttpStatus.OK) {
                addedIte = Optional.of(institutionTicketsRepository.save(ite));
            } else {
                return Optional.empty();
            }
        }
        return addedIte;
    }

    @Transactional
    public Optional<OrderEntity> addNewOrder(HttpEntity<String> order) {
        Optional<OrderEntity> addedOrder = Optional.empty();
        Optional<CreateOrderRequest> orderRequest = Converter.jsonToObject(order.getBody(), CreateOrderRequest.class);
        if (orderRequest.isPresent()) {
            CreateOrderRequest or = orderRequest.get();
            OrderEntity newOrder = new OrderEntity(statusesRepository.getStatusEntityByName("paid"), institutionRepository.getInstitutionId(), or.getSum());
            OrderEntity returnedOrder = ordersRepository.save(newOrder);
            addedOrder = Optional.of(returnedOrder);
            for (InstituteTicketCartItemRequest ticket : or.getTickets()) {
                OrderItemEntity newOrderItem = orderItemEntityMapper(returnedOrder.getOrderId(), ticket);
                orderItemsRepository.save(newOrderItem);
            }
        }
        return addedOrder;
    }

    @Transactional
    public TicketsByTypeResponse[] getTickets() {
        try {
            List<TypeEntity> typesList = typesRepository.findAll();

            return typesList.stream()
                    .map(type -> {
                        TicketsByTypeResponse ticketsByTypeResponse = new TicketsByTypeResponse();
                        ticketsByTypeResponse.setType(type.getName());
                        ticketsByTypeResponse.setDescription(type.getDescription());

                        List<InstitutionTicketEntity> institutionTicketsEntities = institutionTicketsRepository.findByIsActiveTrueAndTicket_Type(type);

                        if (!institutionTicketsEntities.isEmpty()) {
                            List<InstitutionTicket> ticketsList = institutionTicketsEntities.stream()
                                    .map(institutionTicketEntity -> {
                                        TicketEntity ticketEntity = institutionTicketEntity.getTicket();
                                        TimeEntity timeEntity = ticketEntity.getTime();
                                        return new InstitutionTicket(
                                                institutionTicketEntity.getInstitutionTicketId(),
                                                timeEntity != null ? timeEntity.getMinutes() : null,
                                                institutionTicketEntity.getPrice()
                                        );
                                    })
                                    .toList();
                            ticketsByTypeResponse.setInstitutionTickets(ticketsList.toArray(new InstitutionTicket[0]));
                        }
                        return ticketsByTypeResponse;
                    })
                    .toArray(TicketsByTypeResponse[]::new);
        } catch (Exception e) {
            System.err.println("Ошибка во время формирования TicketsByTypeResponse: " + e.getMessage());
            return new TicketsByTypeResponse[0];
        }
    }

    public CouponEntity[] getCoupons() {
        return couponsRepository.findAll().toArray(new CouponEntity[0]);
    }

    public TimeEntity[] getTimes() {
        return timeRepository.findAll().toArray(new TimeEntity[0]);
    }

    public TypeEntity[] getTypes() {
        return typesRepository.findAll().toArray(new TypeEntity[0]);
    }

    public StatusEntity[] getStatuses() {
        return statusesRepository.findAll().toArray(new StatusEntity[0]);
    }

    @Transactional
    public Object updateOrderStatus(HttpEntity<String> httpEntity) {
        Optional<UpdateOrderStatusRequest> request = Converter.jsonToObject(httpEntity.getBody(), UpdateOrderStatusRequest.class);
        if (request.isPresent()) {
            UpdateOrderStatusRequest req = request.get();
            Optional<OrderEntity> oldOrder = Optional.ofNullable(ordersRepository.getOrderEntityByOrderId(req.getOrderId()));
            if (oldOrder.isPresent()) {
                StatusEntity newStatus = statusesRepository.getStatusEntityByStatusId(req.getStatusId());
                if (newStatus != null) {
                    OrderEntity orderToBeUpdated = oldOrder.get();
                    orderToBeUpdated.setStatus(newStatus);
                    orderToBeUpdated.setDateChanged(new Timestamp(System.currentTimeMillis()));
                    OrderEntity returnedOrder;
                    try {
                        returnedOrder = ordersRepository.save(orderToBeUpdated);
                        return returnedOrder;
                    } catch (Exception e) {
                        System.err.println("Ошибка изменения статуса заказа");
                        return new AppError(HttpStatus.NOT_MODIFIED.value(), "Ошибка изменения статуса заказа");
                    }
                } else
                    return new AppError(HttpStatus.NOT_FOUND.value(), "Статус не найден");
            } else
                return new AppError(HttpStatus.NOT_FOUND.value(), "Заказ не найден");
        } else
            return new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неверные данные");
    }

    public Optional<Integer> getSaleByCouponCode(String couponCode) {
        Optional<CouponEntity> couponEntityOptional = Optional.ofNullable(couponsRepository.findTopByCouponCodeOrderByExpirationDateDesc(couponCode));
        if (couponEntityOptional.isPresent()) {
            CouponEntity ce = couponEntityOptional.get();
            ce.setUsed(ce.getUsed() + 1);
            return Optional.of(couponEntityOptional.get().getDiscountAmount());
        } else
            return Optional.empty();
    }

    //    utils

    private static TicketsByOrderResponse CreateTicketsByOrderResponse(OrderItemEntity orderItemEntity) {
        TicketEntity te = orderItemEntity.getInstitutionTicketEntity().getTicket();
        String ticketType = te.getType().getName() + " " + Converter.convertMinutesToHours(te.getTime().getMinutes());
        return new TicketsByOrderResponse(ticketType, orderItemEntity.getOrderItemId(),  orderItemEntity.getInstitutionTicketEntity().getInstitutionTicketId(), orderItemEntity.getTicketsCount());
    }

    private OrderItemEntity orderItemEntityMapper(UUID orderId, InstituteTicketCartItemRequest instituteTicketCartItemRequest) {
        InstitutionTicketEntity ite = institutionTicketsRepository.getInstitutionTicketEntityByInstitutionTicketId(instituteTicketCartItemRequest.getInstitutionTicketId());
        return new OrderItemEntity(orderId, ite, instituteTicketCartItemRequest.getCount());
    }

    private CouponEntity couponEntityMapper(CreateCouponRequest ccr) {
        return new CouponEntity(institutionRepository.getInstitutionId(), ccr.getCode(), ccr.getDiscountAmount(), ccr.getExpirationDate(), ccr.getDescription());
    }

    private InstitutionTicketEntity institutionTicketEntityMapper(CreateInstitutionTicketRequest request) {
        return new InstitutionTicketEntity(institutionRepository.getInstitutionId(), ticketsRepository.findTicketEntityByTicketId(request.getTicketId()), request.getPrice(), request.getExtraValue(), request.getExtraInterval());
    }
}

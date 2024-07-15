package ru.kikopark.localbackend.modules.order.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.kikopark.localbackend.modules.base.service.BaseService;
import ru.kikopark.localbackend.modules.order.dto.OrderAndTicketsResponse;
import ru.kikopark.localbackend.modules.order.entities.*;
import ru.kikopark.localbackend.modules.order.dto.TicketsByTypeResponse;
import ru.kikopark.localbackend.modules.order.entities.TimeEntity;
import ru.kikopark.localbackend.modules.order.service.OrderService;
import ru.kikopark.localbackend.utils.AppError;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@AllArgsConstructor
public class OrderController {
    OrderService orderService;
    BaseService baseService;

    @GetMapping("/guest/coupon")
    public ResponseEntity<Integer> getSaleByCouponCode(@RequestParam String code) {
        Optional<Integer> discountAmount = orderService.getSaleByCouponCode(code);
        return discountAmount.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/guest/tickets")
    public ResponseEntity<TicketsByTypeResponse[]> getTickets() {
        TicketsByTypeResponse[] response = orderService.getTickets();
        if (response.length != 0)
            return new ResponseEntity<>(response, HttpStatus.OK);
        else
            return ResponseEntity.notFound().build();
    }

    @GetMapping("/employee/times")
    public ResponseEntity<TimeEntity[]> getTimes() {
        TimeEntity[] response = orderService.getTimes();
        return (response.length != 0) ? new ResponseEntity<>(response, HttpStatus.OK) : ResponseEntity.notFound().build();
    }

    @GetMapping("/employee/types")
    public ResponseEntity<TypeEntity[]> getTypes() {
        TypeEntity[] response = orderService.getTypes();
        return (response.length != 0) ? new ResponseEntity<>(response, HttpStatus.OK) : ResponseEntity.notFound().build();
    }

    @GetMapping("/employee/statuses")
    public ResponseEntity<StatusEntity[]> getStatuses() {
        StatusEntity[] response = orderService.getStatuses();
        return (response.length != 0) ? new ResponseEntity<>(response, HttpStatus.OK) : ResponseEntity.notFound().build();
    }

    @GetMapping("/admin/tickets-by-order")
    public ResponseEntity<?> getTicketsByOrder(@RequestParam UUID id) {
        Object response = orderService.getTicketsByOrder(id);
        return (response instanceof List<?>) ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                AppError.process(response);
    }

    @GetMapping("/admin/order-and-tickets-from-remote")
    public ResponseEntity<?> getTicketsAndOrderFromRemote(@RequestParam UUID id) {
        Object response = orderService.getTicketsAndOrderFromRemote(id);
        return (response instanceof OrderAndTicketsResponse) ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                AppError.process(response);
    }

    @GetMapping("/employee/order-and-tickets-from-remote")
    public ResponseEntity<?> getTicketsByOrderFromRemote(@RequestParam UUID id) {
        Object response = baseService.getTicketsByOrderFromRemote(id);
        return (response instanceof List<?>) ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                AppError.process(response);
    }

    @PutMapping("/employee/new-order-status")
    public ResponseEntity<?> updateOrderStatus(HttpEntity<String> httpEntity) {
        Object result = orderService.updateOrderStatus(httpEntity);
        return (result instanceof OrderEntity) ? new ResponseEntity<>(HttpStatus.OK) : AppError.process(result);
    }

    @GetMapping("/admin/coupons")
    public ResponseEntity<CouponEntity[]> getCoupons() {
        CouponEntity[] response = orderService.getCoupons();
        return (response.length != 0) ?
                new ResponseEntity<>(response, HttpStatus.OK) :
                ResponseEntity.notFound().build();
    }

    @PostMapping("/admin/new-coupon")
    public ResponseEntity<?> addCoupon(HttpEntity<String> httpEntity) {
        Optional<CouponEntity> insertionSuccess = orderService.addNewCoupon(httpEntity);
        return (insertionSuccess.isPresent()) ?
                new ResponseEntity<>(HttpStatus.OK) :
                new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неправильные данные"), HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/admin/new-order")
    public ResponseEntity<?> addOrder(HttpEntity<String> httpEntity) {
        Optional<OrderEntity> insertionSuccess = orderService.addNewOrder(httpEntity);
        if (insertionSuccess.isPresent()) {
            UUID orderId = insertionSuccess.get().getOrderId();
            return new ResponseEntity<>(orderId, HttpStatus.OK);
        } else
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неправильные данные"), HttpStatus.BAD_REQUEST);
    }

//    @PostMapping("/admin/new-institution-ticket")
//    public ResponseEntity<?> addInstitutionTicket(HttpEntity<String> httpEntity) {
//        Optional<InstitutionTicketEntity> insertionSuccess = orderService.addNewInstitutionTicket(httpEntity);
//        return  (insertionSuccess.isPresent()) ?
//                new ResponseEntity<>(insertionSuccess.get().getInstitutionId(), HttpStatus.OK) :
//                new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), "Отправлены неправильные данные"), HttpStatus.BAD_REQUEST);
//    }
}

package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.OrderEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface OrdersRepository extends JpaRepository<OrderEntity, Integer> {
    OrderEntity getOrderEntityByOrderId(UUID id);
    @Query("SELECT o.status.name FROM OrderEntity o WHERE o.orderId = :id")
    String getStatusNameById(Integer id);

    @Query("SELECT t.type, SUM(oi.ticketsCount) FROM OrderItemEntity oi JOIN oi.institutionTicketEntity it JOIN it.ticket t JOIN OrderEntity o ON oi.orderId = o.orderId " +
            "WHERE o.datePaid BETWEEN :startDate AND :endDate GROUP BY t.type")
    List<Object[]> countTicketsSoldByType(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    @Query("SELECT SUM(o.sum) FROM OrderEntity o WHERE o.datePaid BETWEEN ?1 AND ?2")
    Double sumRevenue(LocalDate startDate, LocalDate endDate);

    @Query("SELECT HOUR(o.datePaid), SUM(o.sum) FROM OrderEntity o WHERE o.datePaid BETWEEN ?1 AND ?2 " +
            "GROUP BY HOUR(o.datePaid)")
    List<Object[]> sumHourlyRevenue(LocalDate startDate, LocalDate endDate);

    @Query("SELECT HOUR(o.datePaid), SUM(oi.ticketsCount) FROM OrderItemEntity oi " +
            "JOIN OrderEntity o ON oi.orderId = o.orderId " +
            "WHERE o.datePaid BETWEEN :startDate AND :endDate " +
            "GROUP BY HOUR(o.datePaid)")
    List<Object[]> countHourlyTicketSales(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);


}

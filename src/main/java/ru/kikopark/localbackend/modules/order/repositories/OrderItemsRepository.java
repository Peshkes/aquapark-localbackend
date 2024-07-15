package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.OrderItemEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemsRepository extends JpaRepository<OrderItemEntity, Integer> {
    List<OrderItemEntity> getOrderItemEntitiesByOrderId(UUID orderId);
    OrderItemEntity findOrderItemEntityByOrderItemId(UUID id);

}

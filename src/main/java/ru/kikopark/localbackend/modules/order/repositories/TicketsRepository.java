package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.TicketEntity;

import java.util.List;
import java.util.UUID;

@Repository
@CacheConfig(cacheNames = "ticketRepository")
public interface TicketsRepository extends JpaRepository<TicketEntity, Integer> {
    @Cacheable(key = "'findTicketEntityByType_TypeId:' + #typeId")
    List<TicketEntity> findTicketEntityByType_TypeId(Integer typeId);
    @Cacheable(key = "'findTicketEntityByClientId:' + #clientId")
    @Query("SELECT DISTINCT t FROM ClientEntity c JOIN c.orderItem oi JOIN oi.institutionTicketEntity it JOIN it.ticket t WHERE c.clientId = :clientId")
    TicketEntity findTicketEntityByClientId(@Param("clientId") UUID clientId);
    @Cacheable(key = "'findTicketEntityByTicketId:' + #id")
    TicketEntity findTicketEntityByTicketId(Integer id);
}

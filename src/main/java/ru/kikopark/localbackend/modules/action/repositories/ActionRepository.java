package ru.kikopark.localbackend.modules.action.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.action.entities.ActionEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface ActionRepository extends JpaRepository<ActionEntity, UUID> {
    List<ActionEntity> findActionEntitiesByClient_ClientId(UUID clientId);

    @Query("SELECT HOUR(a.time), COUNT(a) FROM ActionEntity a WHERE a.actionType.action = 'Вход' " +
            "AND DATE(a.time) = ?1 GROUP BY HOUR(a.time)")
    List<Object[]> countHourlyEntrances(LocalDate date);

    @Query("SELECT HOUR(a.time), COUNT(a) FROM ActionEntity a WHERE a.actionType.action = 'Выход' " +
            "AND DATE(a.time) = ?1 GROUP BY HOUR(a.time)")
    List<Object[]> countHourlyExits(LocalDate date);
}


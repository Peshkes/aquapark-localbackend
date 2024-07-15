package ru.kikopark.localbackend.modules.action.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.action.entities.ExtraEntity;

import java.time.LocalDate;
import java.util.UUID;

@Repository
public interface ExtraRepository extends JpaRepository<ExtraEntity, UUID> {
    ExtraEntity findExtraEntityByClientId(UUID id);

    @Query("SELECT SUM(e.value) FROM ExtraEntity e WHERE e.datePaid BETWEEN ?1 AND ?2")
    Double sumExtraCharges(LocalDate startDate, LocalDate endDate);
}

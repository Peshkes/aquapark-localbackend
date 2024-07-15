package ru.kikopark.localbackend.modules.base.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.base.entities.InstitutionEntity;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InstitutionRepository extends JpaRepository<InstitutionEntity, UUID> {
    @Query("SELECT i.institutionId FROM InstitutionEntity i")
    UUID getInstitutionId();

    Optional<InstitutionEntity> findTopByOrderByInstitutionIdAsc();
}

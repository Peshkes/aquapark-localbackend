package ru.kikopark.localbackend.modules.action.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.action.entities.BraceletEntity;

import java.util.UUID;

@Repository
public interface BraceletRepository extends JpaRepository<BraceletEntity, UUID> {

    BraceletEntity findBraceletEntityByBraceletId(UUID braceletId);
    BraceletEntity findBraceletEntityByNfcTag(String nfcTag);
}

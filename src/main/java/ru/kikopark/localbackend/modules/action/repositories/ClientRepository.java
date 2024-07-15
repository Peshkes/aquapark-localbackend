package ru.kikopark.localbackend.modules.action.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.action.entities.ClientEntity;

import java.util.List;
import java.util.UUID;

@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, UUID> {
    ClientEntity findTopByBracelet_NfcTagOrderByDateCreatedDesc(String nfcTag);

    List<ClientEntity> findClientsByInPark(boolean isIn);
}

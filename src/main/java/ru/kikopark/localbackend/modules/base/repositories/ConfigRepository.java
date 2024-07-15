package ru.kikopark.localbackend.modules.base.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.base.entities.ConfigEntity;

import java.util.Optional;

@Repository
public interface ConfigRepository extends JpaRepository<ConfigEntity, Long> {
    Optional<ConfigEntity> findByConfigKey(String configKey);
}

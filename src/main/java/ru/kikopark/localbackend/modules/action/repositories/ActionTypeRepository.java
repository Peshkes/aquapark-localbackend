package ru.kikopark.localbackend.modules.action.repositories;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.action.entities.ActionTypeEntity;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "actionTypeRepository")
public interface ActionTypeRepository extends JpaRepository<ActionTypeEntity, Integer> {
    @Override
    @Cacheable(key = "'findAll'")
    List<ActionTypeEntity> findAll();
    @Cacheable(key = "'findBy' + #action")
    ActionTypeEntity findActionTypeEntityByAction(String action);
    @Cacheable(key = "'findBy' + #id")
    ActionTypeEntity findActionTypeEntityByActionTypeId(Integer id);
}

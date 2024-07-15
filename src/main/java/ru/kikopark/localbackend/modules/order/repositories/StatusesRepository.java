package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.StatusEntity;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "StatusesRepository")
public interface StatusesRepository extends JpaRepository<StatusEntity, Integer> {
    @Cacheable(key = "'findAll'")
    List<StatusEntity> findAll();
    @Cacheable(key = "'getByName:' + #name")
    StatusEntity getStatusEntityByName(String name);
    @Cacheable(key = "'getById:' + #id")
    StatusEntity getStatusEntityByStatusId(Integer id);
}

package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.TypeEntity;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "typeRepository")
public interface TypesRepository extends JpaRepository<TypeEntity, Integer> {
    @Cacheable(key = "'findAll'")
    List<TypeEntity> findAll();
}

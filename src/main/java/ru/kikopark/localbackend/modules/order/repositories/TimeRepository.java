package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.TimeEntity;

import java.util.List;

@Repository
@CacheConfig(cacheNames = "timeRepository")
public interface TimeRepository extends JpaRepository<TimeEntity, Integer> {
    @Cacheable(key = "'getTimeEntityByTimeId:' + #id")
    TimeEntity getTimeEntityByTimeId(Integer id);

    @Cacheable(key = "'findAll'")
    List<TimeEntity> findAll();
}

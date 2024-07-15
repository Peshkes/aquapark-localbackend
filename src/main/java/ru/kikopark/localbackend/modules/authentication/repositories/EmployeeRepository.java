package ru.kikopark.localbackend.modules.authentication.repositories;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.authentication.entities.EmployeeEntity;

import java.util.UUID;

@Repository
@CacheConfig(cacheNames = "EmployeeRepository")
public interface EmployeeRepository extends JpaRepository<EmployeeEntity, UUID> {
    @Cacheable(key = "'findEmployeeEntityByEmployeeId:' + #id")
    EmployeeEntity findEmployeeEntityByEmployeeId(UUID id);
    @Cacheable(key = "'findEmployeeEntityByEmail:' + #email")
    EmployeeEntity findEmployeeEntityByEmail(String email);
    @Cacheable(key = "'findEmployeeEntityByEmailAndPassword:' + #email + '&' + #password")
    EmployeeEntity findEmployeeEntityByEmailAndPassword(String email, String password);
}

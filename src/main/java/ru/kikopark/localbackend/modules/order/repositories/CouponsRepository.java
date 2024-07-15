package ru.kikopark.localbackend.modules.order.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.kikopark.localbackend.modules.order.entities.CouponEntity;

@Repository
public interface CouponsRepository extends JpaRepository<CouponEntity, Integer> {
    @Query("SELECT c FROM CouponEntity c WHERE c.couponCode = :couponCode ORDER BY c.expirationDate DESC")
    CouponEntity findTopByCouponCodeOrderByExpirationDateDesc(String couponCode);
}

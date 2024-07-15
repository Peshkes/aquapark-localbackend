package ru.kikopark.localbackend.modules.order.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "coupons", schema = "public")
public class CouponEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "coupon_id", updatable = false, nullable = false)
    private UUID couponId;
    @Column(name = "institution_id")
    private UUID institutionId;
    @Column(name = "coupon_code")
    private String couponCode;
    @Column(name = "discount_amount")
    private Integer discountAmount;
    @Column(name = "expiration_date")
    private Date expirationDate;
    private String description;
    @Setter
    private int used;
    public CouponEntity(UUID institutionId, String couponCode, Integer discountAmount, Date expirationDate, String description) {
        this.institutionId = institutionId;
        this.couponCode = couponCode;
        this.discountAmount = discountAmount;
        this.expirationDate = expirationDate;
        this.description = description;
    }
}


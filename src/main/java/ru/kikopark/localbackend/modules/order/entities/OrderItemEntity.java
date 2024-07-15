package ru.kikopark.localbackend.modules.order.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Entity
@NoArgsConstructor
@Table(name = "order_items", schema = "public")
public class OrderItemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "order_item_id", updatable = false, nullable = false)
    private UUID orderItemId;
    @Column(name = "order_id")
    private UUID orderId;
    @ManyToOne
    @JoinColumn(name = "institution_tickets_id", referencedColumnName = "institution_tickets_id")
    private InstitutionTicketEntity institutionTicketEntity;
    @Column(name = "tickets_count")
    private Integer ticketsCount;

    public OrderItemEntity(UUID orderId, InstitutionTicketEntity institutionTicketEntity, Integer ticketsCount) {
        this.orderId = orderId;
        this.institutionTicketEntity = institutionTicketEntity;
        this.ticketsCount = ticketsCount;
    }
}

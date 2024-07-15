package ru.kikopark.localbackend.modules.action.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kikopark.localbackend.modules.order.entities.OrderItemEntity;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "clients")
public class ClientEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "client_id")
    private UUID clientId;
    @ManyToOne
    @JoinColumn(name = "order_item_id", referencedColumnName = "order_item_id")
    private OrderItemEntity orderItem;
    @ManyToOne
    @JoinColumn(name = "bracelet_id", referencedColumnName = "bracelet_id")
    private BraceletEntity bracelet;
    @Setter
    @Column(name = "is_in")
    private boolean inPark;
    @Column(name = "date_created")
    private Timestamp dateCreated;
    @Setter
    @Column(name = "have_extra")
    private boolean haveExtra;
    public ClientEntity(OrderItemEntity orderItem, BraceletEntity bracelet) {
        this.orderItem = orderItem;
        this.bracelet = bracelet;
        this.dateCreated = new Timestamp(System.currentTimeMillis());
        this.inPark = false;
        this.haveExtra = false;
    }
}

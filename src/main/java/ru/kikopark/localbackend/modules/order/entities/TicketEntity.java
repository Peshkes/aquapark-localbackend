package ru.kikopark.localbackend.modules.order.entities;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
@Table(name = "tickets", schema = "public")
public class TicketEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id", updatable = false, nullable = false)
    Integer ticketId;
    @ManyToOne
    @JoinColumn(name = "type_id", referencedColumnName = "type_id")
    TypeEntity type;
    @ManyToOne
    @JoinColumn(name = "time_id", referencedColumnName = "time_id")
    TimeEntity time;
}

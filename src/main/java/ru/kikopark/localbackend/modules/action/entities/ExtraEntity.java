package ru.kikopark.localbackend.modules.action.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Getter
@Table(name = "extras")
@NoArgsConstructor
public class ExtraEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "extra_id")
    private UUID extraId;
    @Column(name = "client_id")
    private UUID clientId;
    private Integer value;
    @Column(name = "date_paid")
    private Timestamp datePaid;

    public ExtraEntity(UUID clientId, Integer value) {
        this.clientId = clientId;
        this.value = value;
        datePaid = new Timestamp(System.currentTimeMillis());
    }
}

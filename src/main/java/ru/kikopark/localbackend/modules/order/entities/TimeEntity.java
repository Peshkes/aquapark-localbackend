package ru.kikopark.localbackend.modules.order.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "times", schema = "public")
public class TimeEntity {
    @Id
    @Column(name = "time_id")
    private Integer timeId;
    private Integer minutes;
}

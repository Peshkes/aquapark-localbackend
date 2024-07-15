package ru.kikopark.localbackend.modules.order.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Getter
@Table(name = "types", schema = "public")
public class TypeEntity {
    @Id
    @Column(name = "type_id")
    private Integer typeId;
    private String name;
    private String description;
}

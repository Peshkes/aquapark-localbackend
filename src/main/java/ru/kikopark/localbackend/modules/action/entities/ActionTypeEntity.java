package ru.kikopark.localbackend.modules.action.entities;

import jakarta.persistence.*;
import lombok.Getter;

import java.util.Objects;

@Entity
@Getter
@Table(name = "action_types", schema = "public")
public class ActionTypeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "action_type_id")
    private Integer actionTypeId;
    private String action;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionTypeEntity that = (ActionTypeEntity) o;
        return Objects.equals(actionTypeId, that.actionTypeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionTypeId);
    }
}

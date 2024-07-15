package ru.kikopark.localbackend.modules.action.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kikopark.localbackend.modules.authentication.entities.EmployeeEntity;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "actions")
@Getter
@NoArgsConstructor
public class ActionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "action_id")
    private UUID actionId;
    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "client_id")
    private ClientEntity client;
    @ManyToOne
    @JoinColumn(name = "action_type_id", referencedColumnName = "action_type_id")
    private ActionTypeEntity actionType;
    @ManyToOne
    @JoinColumn(name = "employee_id", referencedColumnName = "employee_id")
    private EmployeeEntity employee;
    private Timestamp time;

    public ActionEntity(ClientEntity client, ActionTypeEntity actionType, EmployeeEntity employee) {
        this.client = client;
        this.actionType = actionType;
        this.employee = employee;
        this.time = new Timestamp(System.currentTimeMillis());
    }
}

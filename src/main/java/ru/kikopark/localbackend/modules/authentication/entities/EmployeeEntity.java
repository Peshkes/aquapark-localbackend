package ru.kikopark.localbackend.modules.authentication.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "employees", schema = "public")
public class EmployeeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "employee_id", updatable = false, nullable = false)
    private UUID employeeId;
    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "role_id")
    private RoleEntity role;
    @Column(name = "institution_id")
    private UUID institutionId;
    private String name;
    private String password;
    private String email;

    public EmployeeEntity(RoleEntity role, UUID institutionId, String name, String password, String email) {
        this.role = role;
        this.institutionId = institutionId;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    @Override
    public String toString() {
        return "EmployeeEntity{" +
                "employeeId=" + employeeId +
                ", role=" + role +
                ", institutionId=" + institutionId +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}

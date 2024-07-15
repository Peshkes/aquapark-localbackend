package ru.kikopark.localbackend.modules.base.entities;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Table(name = "institutions", schema = "public")
public class InstitutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "institution_id", updatable = false, nullable = false)
    private UUID institutionId;
    private String link;
    private String en_name;
    private String ru_name;
    private String address;

    public InstitutionEntity(String link, String en_name, String ru_name, String address) {
        this.link = link;
        this.en_name = en_name;
        this.ru_name = ru_name;
        this.address = address;
    }
}
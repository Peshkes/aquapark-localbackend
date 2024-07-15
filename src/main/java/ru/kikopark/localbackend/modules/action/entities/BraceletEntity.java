package ru.kikopark.localbackend.modules.action.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "bracelets")
public class BraceletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "bracelet_id")
    private UUID braceletId;
    @Column(name = "nfc_tag")
    private String nfcTag;
    @Column(name = "is_active")
    private Boolean isActive;
    public BraceletEntity(String nfcTag){
        this.nfcTag = nfcTag;
        this.isActive = false;
    };
}

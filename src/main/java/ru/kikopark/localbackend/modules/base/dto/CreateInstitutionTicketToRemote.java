package ru.kikopark.localbackend.modules.base.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.kikopark.localbackend.modules.order.entities.InstitutionTicketEntity;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CreateInstitutionTicketToRemote {
    private UUID institutionId;
    private Integer ticketId;
    private Integer price;
    private Double extraValue;
    private Integer extraInterval;

    public CreateInstitutionTicketToRemote(InstitutionTicketEntity ite){
        this.institutionId = ite.getInstitutionId();
        this.ticketId = ite.getTicket().getTicketId();
        this.price = ite.getPrice();
        this.extraValue = ite.getExtraValue();
        this.extraInterval = ite.getExtraInterval();
    }
}

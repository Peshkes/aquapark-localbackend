package ru.kikopark.localbackend.modules.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class TicketSalesStatistic {
    private Map<String, Integer> ticketsSold;
    private double revenue;
    private double extraCharges;
    private Map<Integer, Double> hourlyRevenue;
    private Map<Integer, Integer> hourlyTicketSales;
    private Map<Integer, Integer> hourlyAttendance;
}

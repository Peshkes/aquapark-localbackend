package ru.kikopark.localbackend.modules.statistics.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.kikopark.localbackend.modules.statistics.dto.TicketSalesStatistic;
import ru.kikopark.localbackend.modules.statistics.service.StatisticsService;

import java.time.LocalDate;
import java.util.Map;

@RestController
@AllArgsConstructor
public class StatisticsController {
    private StatisticsService statisticsService;

    @GetMapping("/statistics/ticket-statistic")
    public TicketSalesStatistic getTicketSalesStatistic(@RequestParam(required = false) String startDate,
                                                        @RequestParam(required = false) String endDate) {
        LocalDate start = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        LocalDate end = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);
        return statisticsService.getTicketSalesStatistic(start, end);
    }

    @GetMapping("/statistics/revenue")
    public double getRevenue(@RequestParam(required = false) String startDate,
                             @RequestParam(required = false) String endDate) {
        LocalDate start = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        LocalDate end = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);
        return statisticsService.getRevenue(start, end);
    }

    @GetMapping("/statistics/extra-charges")
    public double getExtraCharges(@RequestParam(required = false) String startDate,
                                  @RequestParam(required = false) String endDate) {
        LocalDate start = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        LocalDate end = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);
        return statisticsService.getExtraCharges(start, end);
    }

    @GetMapping("/statistics/ticket-sales-by-type")
    public Map<String, Integer> getTicketsSoldByType(@RequestParam(required = false) String startDate,
                                                     @RequestParam(required = false) String endDate) {
        LocalDate start = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        LocalDate end = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);
        return statisticsService.getTicketsSold(start, end);
    }

    @GetMapping("/statistics/hourly-revenue")
    public Map<Integer, Double> getHourlyRevenueByDate(@RequestParam(required = false) String startDate,
                                                       @RequestParam(required = false) String endDate) {
        LocalDate start = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        LocalDate end = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);
        return statisticsService.getHourlyRevenue(start, end);
    }

    @GetMapping("/statistics/hourly-ticket-sales")
    public Map<Integer, Integer> getHourlyTicketSalesByDate(@RequestParam(required = false) String startDate,
                                                            @RequestParam(required = false) String endDate) {
        LocalDate start = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        LocalDate end = (endDate == null) ? LocalDate.now() : LocalDate.parse(endDate);
        return statisticsService.getHourlyTicketSales(start, end);
    }

    @GetMapping("/statistics/hourly-attendance")
    public Map<Integer, Integer> getHourlyAttendanceByDate(@RequestParam(required = false) String startDate) {
        LocalDate date = (startDate == null) ? LocalDate.now() : LocalDate.parse(startDate);
        return statisticsService.getHourlyAttendance(date);
    }
}

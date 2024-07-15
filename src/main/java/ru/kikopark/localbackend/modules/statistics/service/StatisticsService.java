package ru.kikopark.localbackend.modules.statistics.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kikopark.localbackend.modules.action.repositories.ActionRepository;
import ru.kikopark.localbackend.modules.action.repositories.ExtraRepository;
import ru.kikopark.localbackend.modules.order.repositories.OrdersRepository;
import ru.kikopark.localbackend.modules.statistics.dto.TicketSalesStatistic;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class StatisticsService {


    private OrdersRepository orderRepository;
    private ActionRepository actionRepository;
    private  ExtraRepository extraRepository;

    public TicketSalesStatistic getTicketSalesStatistic(LocalDate startDate, LocalDate endDate) {
        TicketSalesStatistic statistic = new TicketSalesStatistic();
        statistic.setTicketsSold(getTicketsSold(startDate, endDate));
        statistic.setRevenue(getRevenue(startDate, endDate));
        statistic.setExtraCharges(getExtraCharges(startDate, endDate));
        statistic.setHourlyRevenue(getHourlyRevenue(startDate, endDate));
        statistic.setHourlyTicketSales(getHourlyTicketSales(startDate, endDate));
        statistic.setHourlyAttendance(getHourlyAttendance(startDate));
        return statistic;
    }

    public Map<String, Integer> getTicketsSold(LocalDate startDate, LocalDate endDate) {
        Map<String, Integer> ticketsSold = new HashMap<>();
        List<Object[]> results = orderRepository.countTicketsSoldByType(startDate, endDate);
        for (Object[] result : results) {
            String type = (String) result[0];
            Integer count = ((Number) result[1]).intValue();
            ticketsSold.put(type, count);
        }
        return ticketsSold;
    }

    public double getRevenue(LocalDate startDate, LocalDate endDate) {
        return orderRepository.sumRevenue(startDate, endDate);
    }

    public double getExtraCharges(LocalDate startDate, LocalDate endDate) {
        return extraRepository.sumExtraCharges(startDate, endDate);
    }

    public Map<Integer, Double> getHourlyRevenue(LocalDate startDate, LocalDate endDate) {
        Map<Integer, Double> hourlyRevenue = new HashMap<>();
        List<Object[]> results = orderRepository.sumHourlyRevenue(startDate, endDate);
        for (Object[] result : results) {
            Integer hour = (Integer) result[0];
            Double revenue = (Double) result[1];
            hourlyRevenue.put(hour, revenue);
        }
        return hourlyRevenue;
    }

    public Map<Integer, Integer> getHourlyTicketSales(LocalDate startDate, LocalDate endDate) {
        Map<Integer, Integer> hourlyTicketSales = new HashMap<>();
        List<Object[]> results = orderRepository.countHourlyTicketSales(startDate, endDate);
        for (Object[] result : results) {
            Integer hour = (Integer) result[0];
            Integer count = ((Number) result[1]).intValue();
            hourlyTicketSales.put(hour, count);
        }
        return hourlyTicketSales;
    }

    public Map<Integer, Integer> getHourlyAttendance(LocalDate date) {
        Map<Integer, Integer> hourlyAttendance = new HashMap<>();
        List<Object[]> entrances = actionRepository.countHourlyEntrances(date);
        List<Object[]> exits = actionRepository.countHourlyExits(date);

        for (Object[] entrance : entrances) {
            Integer hour = (Integer) entrance[0];
            Long entranceCount = (Long) entrance[1];
            hourlyAttendance.put(hour, hourlyAttendance.getOrDefault(hour, 0) + entranceCount.intValue());
        }

        for (Object[] exit : exits) {
            Integer hour = (Integer) exit[0];
            Long exitCount = (Long) exit[1];
            hourlyAttendance.put(hour, hourlyAttendance.getOrDefault(hour, 0) - exitCount.intValue());
        }

        return hourlyAttendance;
    }
}

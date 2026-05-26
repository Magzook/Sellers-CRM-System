package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.magzook.sellersrestservice.dto.enums.TimePeriod;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalListDto;
import ru.magzook.sellersrestservice.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

    @Autowired
    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public SellerWithTotalListDto findTopSeller(TimePeriod period) {
        LocalDateTime[] range = resolveWhenPeriodOfDateBeginsAndEnds(period, LocalDate.now());
        List<SellerWithTotalDto> sellers = analyticsRepository.findTopSellersByPeriod(range[0], range[1]);
        return new SellerWithTotalListDto(sellers);
    }

    public SellerWithTotalListDto findSellersWithTotalBelow(
            LocalDateTime from,
            LocalDateTime to,
            BigDecimal threshold)
    {
        List<SellerWithTotalDto> sellersWithTotalDtos = analyticsRepository
                .findSellersWithTotalBelow(from, to, threshold);
        return new SellerWithTotalListDto(sellersWithTotalDtos);
    }

    /**
     * Example for {@code date} == 2026-02-25:
     * <table>
     *   <tr><th>{@code period}</th>                <th>output</th></tr>
     *   <tr><td>{@link TimePeriod#DAY}</td>        <td>[2026-02-25 00:00:00, 2026-02-26 00:00:00]</td></tr>
     *   <tr><td>{@link TimePeriod#MONTH}</td>      <td>[2026-02-01 00:00:00, 2026-03-01 00:00:00]</td></tr>
     *   <tr><td>{@link TimePeriod#QUARTER}</td>    <td>[2026-01-01 00:00:00, 2026-04-01 00:00:00]</td></tr>
     *   <tr><td>{@link TimePeriod#YEAR}</td>       <td>[2026-01-01 00:00:00, 2027-01-01 00:00:00]</td></tr>
     * </table>
     */
    public static LocalDateTime[] resolveWhenPeriodOfDateBeginsAndEnds(TimePeriod period, LocalDate date) {
        LocalDate[] datesFromInclusiveToExclusive = switch (period) {
            case DAY -> new LocalDate[]{
                    date,
                    date.plusDays(1)
            };
            case MONTH -> new LocalDate[]{
                    date.withDayOfMonth(1),
                    date.withDayOfMonth(1).plusMonths(1)
            };
            case QUARTER -> {
                LocalDate quarterBeginning = date
                        .with(date.getMonth().firstMonthOfQuarter())
                        .withDayOfMonth(1);
                yield new LocalDate[]{
                        quarterBeginning,
                        quarterBeginning.plusMonths(3)
                };
            }
            case YEAR -> new LocalDate[]{
                    date.withDayOfYear(1),
                    date.withDayOfYear(1).plusYears(1)
            };
        };

        return new LocalDateTime[]{
                datesFromInclusiveToExclusive[0].atStartOfDay(),
                datesFromInclusiveToExclusive[1].atStartOfDay()
        };
    }
}

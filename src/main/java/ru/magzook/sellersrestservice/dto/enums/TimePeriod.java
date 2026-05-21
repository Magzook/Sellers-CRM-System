package ru.magzook.sellersrestservice.dto.enums;

import java.time.LocalDate;
import java.time.LocalDateTime;

public enum TimePeriod {
    DAY, MONTH, QUARTER, YEAR;

    public LocalDateTime[] whenBeganAndEnds() {
        LocalDate today = LocalDate.now();

        LocalDate[] datesFromInclusiveToExclusive = switch (this) {
            case DAY -> new LocalDate[]{
                    today,
                    today.plusDays(1)
            };
            case MONTH -> new LocalDate[]{
                    today.withDayOfMonth(1),
                    today.withDayOfMonth(1).plusMonths(1)
            };
            case QUARTER -> {
                LocalDate quarterBeginning = today
                        .with(today.getMonth().firstMonthOfQuarter())
                        .withDayOfMonth(1);
                yield new LocalDate[]{
                        quarterBeginning,
                        quarterBeginning.plusMonths(3)
                };
            }
            case YEAR -> new LocalDate[]{
                    today.withDayOfYear(1),
                    today.withDayOfYear(1).plusYears(1)
            };
        };

        return new LocalDateTime[]{
                datesFromInclusiveToExclusive[0].atStartOfDay(),
                datesFromInclusiveToExclusive[1].atStartOfDay()
        };
    }
}

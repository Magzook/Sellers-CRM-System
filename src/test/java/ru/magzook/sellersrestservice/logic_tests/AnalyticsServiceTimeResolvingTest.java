package ru.magzook.sellersrestservice.logic_tests;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.magzook.sellersrestservice.dto.enums.TimePeriod;
import ru.magzook.sellersrestservice.service.AnalyticsService;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class AnalyticsServiceTimeResolvingTest {

    @ParameterizedTest
    @MethodSource("dayArguments")
    void resolveRange_day(LocalDate date, LocalDateTime expectedFrom, LocalDateTime expectedTo) {
        resolveRange(date, expectedFrom, expectedTo, TimePeriod.DAY);
    }

    @ParameterizedTest
    @MethodSource("monthArguments")
    void resolveRange_month(LocalDate date, LocalDateTime expectedFrom, LocalDateTime expectedTo) {
        resolveRange(date, expectedFrom, expectedTo, TimePeriod.MONTH);
    }

    @ParameterizedTest
    @MethodSource("quarterArguments")
    void resolveRange_quarter(LocalDate date, LocalDateTime expectedFrom, LocalDateTime expectedTo) {
        resolveRange(date, expectedFrom, expectedTo, TimePeriod.QUARTER);
    }

    @ParameterizedTest
    @MethodSource("yearArguments")
    void resolveRange_year(LocalDate date, LocalDateTime expectedFrom, LocalDateTime expectedTo) {
        resolveRange(date, expectedFrom, expectedTo, TimePeriod.YEAR);
    }

    static Stream<Arguments> dayArguments() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2026, 2, 25),
                        LocalDateTime.of(2026, 2, 25, 0, 0),
                        LocalDateTime.of(2026, 2, 26, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 3, 1),
                        LocalDateTime.of(2026, 3, 1, 0, 0),
                        LocalDateTime.of(2026, 3, 2, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 1, 31),
                        LocalDateTime.of(2026, 1, 31, 0, 0),
                        LocalDateTime.of(2026, 2, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 12, 31, 0, 0),
                        LocalDateTime.of(2027, 1, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 1, 1),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 1, 2, 0, 0)
                )
        );
    }

    static Stream<Arguments> monthArguments() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2026, 2, 25),
                        LocalDateTime.of(2026, 2, 1, 0, 0),
                        LocalDateTime.of(2026, 3, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 3, 1),
                        LocalDateTime.of(2026, 3, 1, 0, 0),
                        LocalDateTime.of(2026, 4, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 1, 31),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 2, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 12, 15),
                        LocalDateTime.of(2026, 12, 1, 0, 0),
                        LocalDateTime.of(2027, 1, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 2, 28),
                        LocalDateTime.of(2026, 2, 1, 0, 0),
                        LocalDateTime.of(2026, 3, 1, 0, 0)
                )
        );
    }

    static Stream<Arguments> quarterArguments() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2026, 2, 25),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 4, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 1, 1),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 4, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 3, 31),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 4, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 8, 15),
                        LocalDateTime.of(2026, 7, 1, 0, 0),
                        LocalDateTime.of(2026, 10, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 10, 1, 0, 0),
                        LocalDateTime.of(2027, 1, 1, 0, 0)
                )
        );
    }

    static Stream<Arguments> yearArguments() {
        return Stream.of(
                Arguments.of(
                        LocalDate.of(2026, 2, 25),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2027, 1, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 1, 1),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2027, 1, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2026, 12, 31),
                        LocalDateTime.of(2026, 1, 1, 0, 0),
                        LocalDateTime.of(2027, 1, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2024, 2, 29),
                        LocalDateTime.of(2024, 1, 1, 0, 0),
                        LocalDateTime.of(2025, 1, 1, 0, 0)
                ),
                Arguments.of(
                        LocalDate.of(2025, 12, 31),
                        LocalDateTime.of(2025, 1, 1, 0, 0),
                        LocalDateTime.of(2026, 1, 1, 0, 0)
                )
        );
    }

    private void resolveRange(
            LocalDate date,
            LocalDateTime expectedFrom,
            LocalDateTime expectedTo,
            TimePeriod timePeriod) {
        LocalDateTime[] range = AnalyticsService.resolveWhenPeriodOfDateBeginsAndEnds(timePeriod, date);

        assertThat(range[0]).isEqualTo(expectedFrom);
        assertThat(range[1]).isEqualTo(expectedTo);
    }
}
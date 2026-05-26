package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.ResultActions;
import ru.magzook.sellersrestservice.dto.enums.TimePeriod;
import ru.magzook.sellersrestservice.service.AnalyticsService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AnalyticsControllerTest extends BaseIntegrationTest {

    private static final String top1SellersUrl = baseUrl + "/analytics/top-1-sellers";
    private static final String sellersBelowThresholdUrl = baseUrl + "/analytics/sellers-below-threshold";

    @Test
    void getTopSellers_singleWinner() throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        int bob = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(1000, "CASH", alice);
        helper.createTransaction(500, "CASH", alice);
        helper.createTransaction(30, "CARD", bob);
        helper.createTransaction(20, "CARD", bob);
        helper.createTransaction(300, "CARD", bob);

        mockMvc.perform(get(top1SellersUrl).param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"))
                .andExpect(jsonPath("$.sellers[0].total").value(1500.0));
    }

    @Test
    void getTopSellers_tiedWinners() throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        int bob = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(10000, "CASH", alice);
        helper.createTransaction(7000, "CASH", alice);
        helper.createTransaction(5000, "CARD", bob);
        helper.createTransaction(12000, "CARD", bob);

        mockMvc.perform(get(top1SellersUrl).param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(2))
                .andExpect(jsonPath("$.sellers[*].name", containsInAnyOrder("Alice", "Bob")))
                .andExpect(jsonPath("$.sellers[*].total", containsInAnyOrder(17000.0, 17000.0)));
    }

    @Test
    void getTopSellers_noTransactions_returnsEmptyList() throws Exception {
        helper.createSeller("Alice", "alice@mail.com");

        mockMvc.perform(get(top1SellersUrl).param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(0));
    }

    @ParameterizedTest
    @EnumSource(TimePeriod.class)
    void getTopSellers_allPeriods_return200(TimePeriod period) throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        helper.createTransaction(10000, "CASH", alice);

        mockMvc.perform(get(top1SellersUrl).param("period", period.name()))
                .andExpect(status().isOk());
    }

    @Test
    void getTopSellers_invalidPeriod_returns400() throws Exception {
        String argumentName = "period";
        String argumentType = TimePeriod.class.getSimpleName();
        String actualValue = "WEEK";
        ResultActions response = mockMvc.perform(get(top1SellersUrl).param(argumentName, actualValue));
        expectMethodArgumentTypeMismatch(response, argumentName, argumentType, actualValue);
    }

    @ParameterizedTest
    @MethodSource("periodArguments")
    void getTopSellers_onlyTransactionsFromCurrentPeriodCounted(
            TimePeriod period,
            LocalDateTime insidePeriod,
            LocalDateTime outsidePeriod) throws Exception {

        int alice = helper.createSeller("Alice", "alice@mail.com");
        int bob = helper.createSeller("Bob", "bob@mail.com");

        helper.createTransactionWithDate(10000, "CASH", alice, insidePeriod);
        helper.createTransactionWithDate(5000, "CARD", bob, insidePeriod);
        helper.createTransactionWithDate(99999, "CASH", bob, outsidePeriod);

        mockMvc.perform(get(top1SellersUrl).param("period", period.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"));
    }

    private static Stream<Arguments> periodArguments() {
        return Stream.of(TimePeriod.values())
                .map(period -> {
                    LocalDateTime[] range = AnalyticsService
                            .resolveWhenPeriodOfDateBeginsAndEnds(period, LocalDate.now());
                    LocalDateTime insidePeriod = range[0].plusHours(1);
                    LocalDateTime outsidePeriod = range[0].minusDays(1);
                    return Arguments.of(period, insidePeriod, outsidePeriod);
                });
    }

    @Test
    void getSellersBelowThreshold_success() throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        int bob = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(3000, "CASH", alice);
        helper.createTransaction(7000, "CARD", bob);

        mockMvc.perform(get(sellersBelowThresholdUrl)
                        .param("from", "2000-01-01 00:00:00")
                        .param("to", "9999-12-31 23:59:59")
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"))
                .andExpect(jsonPath("$.sellers[0].total").value(3000));
    }

    @Test
    void getSellersBelowThreshold_allMatch() throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        int bob = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(1000, "CASH", alice);
        helper.createTransaction(2000, "CARD", bob);

        mockMvc.perform(get(sellersBelowThresholdUrl)
                        .param("from", "2000-01-01 00:00:00")
                        .param("to", "9999-12-31 23:59:59")
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(2))
                .andExpect(jsonPath("$.sellers[*].name", containsInAnyOrder("Alice", "Bob")));
    }

    @Test
    void getSellersBelowThreshold_noneMatch_returnsEmptyList() throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        helper.createTransaction(10000, "CASH", alice);

        mockMvc.perform(get(sellersBelowThresholdUrl)
                        .param("from", "2026-01-01 00:00:00")
                        .param("to", "2026-12-31 23:59:59")
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(0));
    }

    @Test
    void getSellersBelowThreshold_invalidDateFormat_returns400() throws Exception {
        String argumentName = "from";
        String argumentType = LocalDateTime.class.getSimpleName();
        String actualValue = "01-01-2026";
        ResultActions response = mockMvc.perform(get(sellersBelowThresholdUrl)
                        .param(argumentName, actualValue)
                        .param("to", "2026-12-31 23:59:59")
                        .param("threshold", "5000"));
        expectMethodArgumentTypeMismatch(response, argumentName, argumentType, actualValue);
    }

    @Test
    void getSellersBelowThreshold_onlyTransactionsFromPeriodCounted() throws Exception {
        int alice = helper.createSeller("Alice", "alice@mail.com");
        int bob = helper.createSeller("Bob", "bob@mail.com");

        LocalDateTime thisYear = LocalDateTime.now().withDayOfYear(1);
        LocalDateTime lastYear = thisYear.minusYears(1);

        helper.createTransactionWithDate(3000, "CASH", alice, thisYear);
        helper.createTransactionWithDate(7000, "CARD", bob, thisYear);
        helper.createTransactionWithDate(99999, "CASH", alice, lastYear);

        String fromStr = thisYear.format(helper.dateTimeFormatter);
        String toStr = thisYear.plusYears(1).format(helper.dateTimeFormatter);

        mockMvc.perform(get(sellersBelowThresholdUrl)
                        .param("from", fromStr)
                        .param("to", toStr)
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"))
                .andExpect(jsonPath("$.sellers[0].total").value(3000));
    }
}
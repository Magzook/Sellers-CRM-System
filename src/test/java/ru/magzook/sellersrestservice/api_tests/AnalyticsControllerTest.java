package ru.magzook.sellersrestservice.api_tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.test.web.servlet.ResultActions;
import ru.magzook.sellersrestservice.dto.enums.PaymentType;
import ru.magzook.sellersrestservice.dto.enums.TimePeriod;
import ru.magzook.sellersrestservice.service.AnalyticsService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.SELLERS_BELOW_THRESHOLD;
import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.TOP_1_SELLERS;

public class AnalyticsControllerTest extends BaseIntegrationTest {

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    @Test
    void getTopSellers_singleWinner() throws Exception {
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        int bobId = createHelper.createSeller("Bob", "bob@mail.com");
        createHelper.createTransaction(new BigDecimal("1000"), PaymentType.CASH, aliceId);
        createHelper.createTransaction(new BigDecimal("500"), PaymentType.CASH, aliceId);
        createHelper.createTransaction(new BigDecimal("30"), PaymentType.CARD, bobId);
        createHelper.createTransaction(new BigDecimal("20"), PaymentType.CARD, bobId);
        createHelper.createTransaction(new BigDecimal("300"), PaymentType.CARD, bobId);

        mockMvc.perform(get(TOP_1_SELLERS).param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"))
                .andExpect(jsonPath("$.sellers[0].total").value(new BigDecimal("1500.0")));
    }

    @Test
    void getTopSellers_tiedWinners() throws Exception {
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        int bobId = createHelper.createSeller("Bob", "bob@mail.com");
        createHelper.createTransaction(new BigDecimal("10000"), PaymentType.CASH, aliceId);
        createHelper.createTransaction(new BigDecimal("7000"), PaymentType.CASH, aliceId);
        createHelper.createTransaction(new BigDecimal("5000"), PaymentType.CARD, bobId);
        createHelper.createTransaction(new BigDecimal("12000"), PaymentType.CARD, bobId);

        mockMvc.perform(get(TOP_1_SELLERS).param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(2))
                .andExpect(jsonPath("$.sellers[*].name", containsInAnyOrder("Alice", "Bob")))
                .andExpect(jsonPath("$.sellers[0].total").value(new BigDecimal("17000.0")))
                .andExpect(jsonPath("$.sellers[1].total").value(new BigDecimal("17000.0")));
    }

    @Test
    void getTopSellers_noTransactions_returnsEmptyList() throws Exception {
        createHelper.createSeller("Alice", "alice@mail.com");

        mockMvc.perform(get(TOP_1_SELLERS).param("period", "DAY"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(0));
    }

    @ParameterizedTest
    @EnumSource(TimePeriod.class)
    void getTopSellers_allPeriods_return200(TimePeriod period) throws Exception {
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        createHelper.createTransaction(new BigDecimal("10000"), PaymentType.CASH, aliceId);

        mockMvc.perform(get(TOP_1_SELLERS).param("period", period.name()))
                .andExpect(status().isOk());
    }

    @Test
    void getTopSellers_invalidPeriod_returns400() throws Exception {
        String argumentName = "period";
        String argumentType = TimePeriod.class.getSimpleName();
        String actualValue = "WEEK";
        ResultActions response = mockMvc.perform(get(TOP_1_SELLERS).param(argumentName, actualValue));
        expectHelper.expectMethodArgumentTypeMismatch(response, argumentName, argumentType, actualValue);
    }

    @ParameterizedTest
    @MethodSource("periodArguments")
    void getTopSellers_onlyTransactionsFromCurrentPeriodCounted(
            TimePeriod period,
            LocalDateTime insidePeriod,
            LocalDateTime outsidePeriod) throws Exception {

        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        int bobId = createHelper.createSeller("Bob", "bob@mail.com");

        createHelper.createTransactionWithDate(new BigDecimal("10000"), PaymentType.CASH, aliceId, insidePeriod);
        createHelper.createTransactionWithDate(new BigDecimal("5000"), PaymentType.CASH, bobId, insidePeriod);
        createHelper.createTransactionWithDate(new BigDecimal("99999"), PaymentType.CASH, bobId, outsidePeriod);

        mockMvc.perform(get(TOP_1_SELLERS).param("period", period.name()))
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
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        int bobId = createHelper.createSeller("Bob", "bob@mail.com");
        createHelper.createTransaction(new BigDecimal("3000"), PaymentType.CASH, aliceId);
        createHelper.createTransaction(new BigDecimal("7000"), PaymentType.CARD, bobId);

        mockMvc.perform(get(SELLERS_BELOW_THRESHOLD)
                        .param("from", "2000-01-01 00:00:00")
                        .param("to", "9999-12-31 23:59:59")
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"))
                .andExpect(jsonPath("$.sellers[0].total").value(new BigDecimal("3000.0")));
    }

    @Test
    void getSellersBelowThreshold_allMatch() throws Exception {
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        int bobId = createHelper.createSeller("Bob", "bob@mail.com");
        createHelper.createTransaction(new BigDecimal("1000"), PaymentType.CASH, aliceId);
        createHelper.createTransaction(new BigDecimal("2000"), PaymentType.CARD, bobId);

        mockMvc.perform(get(SELLERS_BELOW_THRESHOLD)
                        .param("from", "2000-01-01 00:00:00")
                        .param("to", "9999-12-31 23:59:59")
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(2))
                .andExpect(jsonPath("$.sellers[*].name", containsInAnyOrder("Alice", "Bob")));
    }

    @Test
    void getSellersBelowThreshold_noneMatch_returnsEmptyList() throws Exception {
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        createHelper.createTransaction(new BigDecimal("10000"), PaymentType.CASH, aliceId);

        mockMvc.perform(get(SELLERS_BELOW_THRESHOLD)
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
        ResultActions response = mockMvc.perform(get(SELLERS_BELOW_THRESHOLD)
                        .param(argumentName, actualValue)
                        .param("to", "2026-12-31 23:59:59")
                        .param("threshold", "5000"));
        expectHelper.expectMethodArgumentTypeMismatch(response, argumentName, argumentType, actualValue);
    }

    @Test
    void getSellersBelowThreshold_onlyTransactionsFromPeriodCounted() throws Exception {
        int aliceId = createHelper.createSeller("Alice", "alice@mail.com");
        int bobId = createHelper.createSeller("Bob", "bob@mail.com");

        LocalDateTime thisYear = LocalDateTime.now().withDayOfYear(1);
        LocalDateTime lastYear = thisYear.minusYears(1);

        createHelper.createTransactionWithDate(new BigDecimal("3000"), PaymentType.CASH, aliceId, thisYear);
        createHelper.createTransactionWithDate(new BigDecimal("7000"), PaymentType.CASH, bobId, thisYear);
        createHelper.createTransactionWithDate(new BigDecimal("99999"), PaymentType.CASH, aliceId, lastYear);

        mockMvc.perform(get(SELLERS_BELOW_THRESHOLD)
                        .param("from", thisYear.format(dateTimeFormatter))
                        .param("to", thisYear.plusYears(1).format(dateTimeFormatter))
                        .param("threshold", "5000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(1))
                .andExpect(jsonPath("$.sellers[0].name").value("Alice"))
                .andExpect(jsonPath("$.sellers[0].total").value(new BigDecimal("3000.0")));
    }
}
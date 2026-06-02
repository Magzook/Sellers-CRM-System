package ru.magzook.sellersrestservice.api_tests.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.magzook.sellersrestservice.dto.enums.PaymentType;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.SELLERS;
import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.TRANSACTIONS;

@Component
public class CrudHelper {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HttpRequestHelper requestHelper;

    @Autowired
    private BodyHelper bodyHelper;

    public int createSeller(String name, String contactInfo) throws Exception {
        String requestBody = bodyHelper.makeSellerBody(name, contactInfo);

        var response = requestHelper.post(SELLERS, requestBody);
        String responseBody = response.andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("id").asInt();
    }

    public long createTransaction(BigDecimal amount, PaymentType paymentType, int sellerId) throws Exception {
        String requestBody = bodyHelper.makeTransactionBody(amount, paymentType, sellerId);

        var response = requestHelper.post(TRANSACTIONS, requestBody);
        String responseBody = response.andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(responseBody).get("id").asLong();
    }

    /**
     Directly uses the database because date cannot be included in http request
     */
    public void createTransactionWithDate(BigDecimal amount, PaymentType paymentType, int sellerId, LocalDateTime date) {
        jdbcTemplate.update(
                "INSERT INTO transactions (amount, payment_type, seller_id, transaction_date) VALUES (?, ?, ?, ?)",
                amount, paymentType.toString(), sellerId, date
        );
    }
}

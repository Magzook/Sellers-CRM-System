package ru.magzook.sellersrestservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Component
public class TestHelper {

    public final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void createTransactionWithDate(int amount, String paymentType, int sellerId, LocalDateTime date) {
        jdbcTemplate.update(
                "INSERT INTO transactions (amount, payment_type, seller_id, transaction_date) VALUES (?, ?, ?, ?)",
                amount, paymentType, sellerId, date
        );
    }

    public int createSeller(String name, String contactInfo) throws Exception {
        return postAndReturnIntId("/api/v1/sellers",
                                """
                                {"name": "%s", "contactInfo": "%s"}
                                """.formatted(name, contactInfo));
    }

    public int createTransaction(int amount, String paymentType, int sellerId) throws Exception {
        return postAndReturnIntId("/api/v1/transactions",
                                """
                                {"amount": %s, "paymentType": "%s", "sellerId": %d}
                                """.formatted(amount, paymentType, sellerId));
    }

    private int postAndReturnIntId(String url, String content) throws Exception {
        String response = mockMvc.perform(post(url)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asInt();
    }
}

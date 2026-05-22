package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest extends BaseIntegrationTest {

    private static final String transactionsUrl = baseUrl + "/transactions";

    @Test
    void createTransaction_success() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");

        mockMvc.perform(post(transactionsUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 100.50, "paymentType": "CASH", "sellerId": %d}
                                """.formatted(sellerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.paymentType").value("CASH"))
                .andExpect(jsonPath("$.transactionDate").isNotEmpty())
                .andExpect(jsonPath("$.sellerId").value(sellerId));
    }

    @Test
    void createTransaction_negativeAmount_returns400() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");

        mockMvc.perform(post(transactionsUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": -1, "paymentType": "CASH", "sellerId": %d}
                                """.formatted(sellerId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("amount cannot be negative"));
    }

    @Test
    void createTransaction_invalidPaymentType_returns400() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");

        mockMvc.perform(post(transactionsUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 100, "paymentType": "BITCOIN", "sellerId": %d}
                                """.formatted(sellerId)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Unreadable enum value"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("Accepted values are: [CASH, CARD, TRANSFER]"));
    }

    @Test
    void createTransaction_sellerNotFound_returns404() throws Exception {
        mockMvc.perform(post(transactionsUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": 100, "paymentType": "CASH", "sellerId": 999}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("seller with id 999 not found"));
    }

    @Test
    void getTransaction_withSellerInfo() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");
        int txId = helper.createTransaction(100, "CARD", sellerId);

        mockMvc.perform(get(transactionsUrl + "/{id}", txId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(100))
                .andExpect(jsonPath("$.paymentType").value("CARD"))
                .andExpect(jsonPath("$.transactionDate").isNotEmpty())
                .andExpect(jsonPath("$.seller.id").value(sellerId))
                .andExpect(jsonPath("$.seller.name").value("Bob"))
                .andExpect(jsonPath("$.seller.contactInfo").value("bob@mail.com"))
                .andExpect(jsonPath("$.seller.registrationDate").isNotEmpty());
    }

    @Test
    void getTransactionsBySeller_success() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(100, "CASH", sellerId);
        helper.createTransaction(200, "CARD", sellerId);
        helper.createTransaction(300, "TRANSFER", sellerId);

        mockMvc.perform(get(baseUrl + "/sellers/{id}/transactions", sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.length()").value(3));
    }

    @Test
    void deleteSeller_cascadeDeletesTransactions() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(100, "CASH", sellerId);
        helper.createTransaction(200, "CARD", sellerId);
        helper.createTransaction(300, "TRANSFER", sellerId);

        mockMvc.perform(delete(baseUrl + "/sellers/{id}", sellerId))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/sellers/{id}/transactions", sellerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getTransaction_notFound_returns404() throws Exception {
        mockMvc.perform(get(transactionsUrl + "/{id}", 999))
                .andExpect(status().isNotFound());
    }
}
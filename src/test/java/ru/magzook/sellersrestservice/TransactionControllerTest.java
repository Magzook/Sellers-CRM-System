package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest extends BaseIntegrationTest {

    private static final String transactionsUrl = baseUrl + "/transactions";

    @Test
    void createTransaction_success() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");
        performPost(transactionsUrl, makeTransactionBody("100.50", "CASH", Integer.toString(sellerId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(100.50))
                .andExpect(jsonPath("$.paymentType").value("CASH"))
                .andExpect(jsonPath("$.transactionDate").isNotEmpty())
                .andExpect(jsonPath("$.sellerId").value(sellerId));
    }

    @Test
    void createTransaction_negativeAmount_returns400() throws Exception {
        createTransaction_InvalidAmount("-18", "amount cannot be negative");
    }

    @Test
    void createTransaction_InvalidNumericAmount_returns400() throws Exception {
        createTransaction_InvalidAmount("777777777777777777777", "amount must fit numeric(14,2)");
    }

    @Test
    void createTransaction_InvalidNumericAmount2_returns400() throws Exception {
        createTransaction_InvalidAmount("777.192", "amount must fit numeric(14,2)");
    }

    @Test
    void createTransaction_invalidPaymentType_returns400() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");

        var response = performPost(
                transactionsUrl,
                makeTransactionBody("100", "BITCOIN", Integer.toString(sellerId)));
        expectBadEnumInJson(response, "Accepted values are: [CASH, CARD, TRANSFER]");
    }

    @Test
    void createTransaction_sellerNotFound_returns404() throws Exception {
        int sellerId = 999;
        var response = performPost(
                transactionsUrl,
                makeTransactionBody("100", "CASH", Integer.toString(sellerId)));
        expectEntityWithIdNotFound(response, "seller", sellerId);
    }

    @Test
    void getTransaction_withSellerInfo() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");
        int transactionId = helper.createTransaction(100, "CARD", sellerId);

        performGet(transactionsUrl + "/" + transactionId)
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

        performGet(baseUrl + "/sellers/" + sellerId + "/transactions")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.length()").value(3));
    }

    @Test
    void deleteSeller_cascadeDeletesTransactions() throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");
        helper.createTransaction(100, "CASH", sellerId);
        helper.createTransaction(200, "CARD", sellerId);
        helper.createTransaction(300, "TRANSFER", sellerId);

        performDelete(baseUrl + "/sellers/" + sellerId)
                .andExpect(status().isOk());

        var response = performGet(baseUrl + "/sellers/" + sellerId + "/transactions");
        expectEntityWithIdNotFound(response, "seller", sellerId);
    }

    @Test
    void getTransaction_notFound_returns404() throws Exception {
        int id = 999;
        var response = performGet(transactionsUrl + "/" + id);
        expectEntityWithIdNotFound(response, "transaction", id);
    }

    private void createTransaction_InvalidAmount(String amountAsString, String detail) throws Exception {
        int sellerId = helper.createSeller("Bob", "bob@mail.com");

        var response = performPost(
                transactionsUrl,
                makeTransactionBody(amountAsString, "CASH", Integer.toString(sellerId)));
        expectValidationFailed(response, detail);
    }

    private static String makeTransactionBody(
            String amountAsString,
            String paymentTypeAsString,
            String sellerIdAsString) {
        return """
               {"amount": %s, "paymentType": "%s", "sellerId": %s}
               """.formatted(amountAsString, paymentTypeAsString, sellerIdAsString);
    }
}
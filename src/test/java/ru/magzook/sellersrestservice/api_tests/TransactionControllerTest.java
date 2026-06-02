package ru.magzook.sellersrestservice.api_tests;

import org.junit.jupiter.api.Test;
import ru.magzook.sellersrestservice.dto.enums.PaymentType;
import java.math.BigDecimal;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.*;

public class TransactionControllerTest extends BaseIntegrationTest {

    private static final String ERROR_AMOUNT_NEGATIVE = "amount cannot be negative";
    private static final String ERROR_AMOUNT_BAD_NUMERIC = "amount must fit numeric(14,2)";

    @Test
    void createTransaction_success() throws Exception {
        int sellerId = crudHelper.createSeller("Bob", "bob@mail.com");
        BigDecimal amount = new BigDecimal("100.55");
        PaymentType paymentType = PaymentType.CASH;
        httpRequestHelper.post(TRANSACTIONS, bodyHelper.makeTransactionBody(amount, paymentType, sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.paymentType").value(paymentType.toString()))
                .andExpect(jsonPath("$.transactionDate").isNotEmpty())
                .andExpect(jsonPath("$.sellerId").value(sellerId));
    }

    @Test
    void createTransaction_negativeAmount_returns400() throws Exception {
        createTransaction_InvalidAmount(new BigDecimal("-18"), ERROR_AMOUNT_NEGATIVE);
    }

    @Test
    void createTransaction_InvalidNumericAmount_returns400() throws Exception {
        createTransaction_InvalidAmount(new BigDecimal("77777777777777777777777"), ERROR_AMOUNT_BAD_NUMERIC);
    }

    @Test
    void createTransaction_InvalidNumericAmount2_returns400() throws Exception {
        createTransaction_InvalidAmount(new BigDecimal("777.192"), ERROR_AMOUNT_BAD_NUMERIC);
    }

    @Test
    void createTransaction_sellerNotFound_returns404() throws Exception {
        int sellerId = 999;
        var response = httpRequestHelper.post(
                TRANSACTIONS,
                bodyHelper.makeTransactionBody(new BigDecimal("100"), PaymentType.CASH, sellerId));
        expectHelper.expectEntityWithIdNotFound(response, "seller", sellerId);
    }

    @Test
    void getTransaction_withSellerInfo() throws Exception {
        String sellerName = "Bob";
        String sellerEmail = "bob@mail.com";
        int sellerId = crudHelper.createSeller(sellerName, sellerEmail);

        BigDecimal amount = new BigDecimal("100.55");
        PaymentType paymentType = PaymentType.CARD;
        long transactionId = crudHelper.createTransaction(amount, paymentType, sellerId);

        httpRequestHelper.get(transactionsSlashId(transactionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.amount").value(amount))
                .andExpect(jsonPath("$.paymentType").value(paymentType.toString()))
                .andExpect(jsonPath("$.transactionDate").isNotEmpty())
                .andExpect(jsonPath("$.seller.id").value(sellerId))
                .andExpect(jsonPath("$.seller.name").value(sellerName))
                .andExpect(jsonPath("$.seller.contactInfo").value(sellerEmail))
                .andExpect(jsonPath("$.seller.registrationDate").isNotEmpty());
    }

    @Test
    void getTransactionsBySeller_success() throws Exception {
        int sellerId = crudHelper.createSeller("Bob", "bob@mail.com");
        crudHelper.createTransaction(new BigDecimal("100"), PaymentType.CASH, sellerId);
        crudHelper.createTransaction(new BigDecimal("200"), PaymentType.CARD, sellerId);
        crudHelper.createTransaction(new BigDecimal("100"), PaymentType.TRANSFER, sellerId);

        httpRequestHelper.get(sellersSlashIdSlashTransactions(sellerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.transactions.length()").value(3));
    }

    @Test
    void deleteSeller_cascadeDeletesTransactions() throws Exception {
        int sellerId = crudHelper.createSeller("Bob", "bob@mail.com");
        crudHelper.createTransaction(new BigDecimal("100"), PaymentType.CASH, sellerId);
        crudHelper.createTransaction(new BigDecimal("200"), PaymentType.CARD, sellerId);
        crudHelper.createTransaction(new BigDecimal("100"), PaymentType.TRANSFER, sellerId);

        httpRequestHelper.delete(sellersSlashId(sellerId))
                .andExpect(status().isOk());

        var response = httpRequestHelper.get(sellersSlashIdSlashTransactions(sellerId));
        expectHelper.expectEntityWithIdNotFound(response, "seller", sellerId);
    }

    @Test
    void getTransaction_notFound_returns404() throws Exception {
        int id = 999;
        var response = httpRequestHelper.get(transactionsSlashId(id));
        expectHelper.expectEntityWithIdNotFound(response, "transaction", id);
    }

    private void createTransaction_InvalidAmount(BigDecimal amount, String detail) throws Exception {
        int sellerId = crudHelper.createSeller("Bob", "bob@mail.com");

        var response = httpRequestHelper.post(
                TRANSACTIONS,
                bodyHelper.makeTransactionBody(amount, PaymentType.CASH, sellerId));
        expectHelper.expectValidationFailed(response, detail);
    }
}
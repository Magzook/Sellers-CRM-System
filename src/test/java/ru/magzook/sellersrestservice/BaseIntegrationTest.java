package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.magzook.sellersrestservice.repository.SellerRepository;
import ru.magzook.sellersrestservice.repository.TransactionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected TestHelper helper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    protected final String baseUrl = "/api/v1";

    @AfterEach
    void cleanup() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
    }
}
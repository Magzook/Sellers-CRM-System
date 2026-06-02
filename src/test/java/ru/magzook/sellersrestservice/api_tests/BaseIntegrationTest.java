package ru.magzook.sellersrestservice.api_tests;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import ru.magzook.sellersrestservice.api_tests.helpers.BodyHelper;
import ru.magzook.sellersrestservice.api_tests.helpers.CrudHelper;
import ru.magzook.sellersrestservice.api_tests.helpers.ExpectHelper;
import ru.magzook.sellersrestservice.api_tests.helpers.HttpRequestHelper;
import ru.magzook.sellersrestservice.repository.SellerRepository;
import ru.magzook.sellersrestservice.repository.TransactionRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected CrudHelper crudHelper;

    @Autowired
    protected BodyHelper bodyHelper;

    @Autowired
    protected HttpRequestHelper httpRequestHelper;

    @Autowired
    protected ExpectHelper expectHelper;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private SellerRepository sellerRepository;

    @AfterEach
    void cleanup() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
    }
}
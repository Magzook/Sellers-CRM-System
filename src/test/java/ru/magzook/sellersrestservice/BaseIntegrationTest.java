package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import ru.magzook.sellersrestservice.repository.SellerRepository;
import ru.magzook.sellersrestservice.repository.TransactionRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    protected static final String baseUrl = "/api/v1";

    @AfterEach
    void cleanup() {
        transactionRepository.deleteAll();
        sellerRepository.deleteAll();
    }

    public ResultActions performGet(String url) throws Exception {
        return mockMvc.perform(get(url));
    }

    public ResultActions performPost(String url,String content) throws Exception {
        return mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    public ResultActions performPut(String url, String content) throws Exception {
        return mockMvc.perform(put(url)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content));
    }

    public ResultActions performDelete(String url) throws Exception {
        return mockMvc.perform(delete(url));
    }

    public void expectValidationFailed(ResultActions response, String... details) throws Exception {
        expect_BadRequest_OneDetail(response, "Validation failed", details[0]);
    }

    public void expectBadEnumInJson(ResultActions response, String... details) throws Exception {
        expect_BadRequest_OneDetail(response, "Unreadable enum value", details[0]);
    }

    public void expectMethodArgumentTypeMismatch(
            ResultActions response,
            String argumentName,
            String argumentType,
            String actualValueAsString) throws Exception {
        String detail = "%s should be a valid %s and %s isn't"
                .formatted(argumentName, argumentType, actualValueAsString);
        expect_BadRequest_OneDetail(response, "Method argument type mismatch", detail);
    }

    public void expectEntityWithIdNotFound(ResultActions response, String entityName, Object id) throws Exception {
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("%s with id %s not found".formatted(entityName, id.toString())))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details.length()").value(0));
    }

    private void expect_BadRequest_OneDetail(
            ResultActions response,
            String message,
            String firstDetail) throws Exception {
        response.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value(firstDetail));
    }
}
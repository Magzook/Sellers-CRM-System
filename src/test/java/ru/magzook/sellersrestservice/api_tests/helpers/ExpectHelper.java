package ru.magzook.sellersrestservice.api_tests.helpers;

import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class ExpectHelper {

    public void expectEntityWithIdNotFound(ResultActions response, String entityName, Object id) throws Exception {
        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message")
                        .value("%s with id %s not found".formatted(entityName, id.toString())))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details.length()").value(0));
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

    public void expectValidationFailed(ResultActions response, String... details) throws Exception {
        expect_BadRequest_OneDetail(response, "Validation failed", details[0]);
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

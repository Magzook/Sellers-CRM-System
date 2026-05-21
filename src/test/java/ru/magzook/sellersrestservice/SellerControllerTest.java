package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SellerControllerTest extends BaseIntegrationTest {

    @Autowired
    private TestHelper helper;

    private final String baseUrl = "/api/v1";

    @Test
    void createSeller_success() throws Exception {
        String name = "John";
        String contactInfo = "john@mail.com";
        mockMvc.perform(post(baseUrl + "/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "contactInfo": "%s"}
                                """.formatted(name, contactInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.contactInfo").value(contactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void createSeller_nameIsBlank_returns400() throws Exception {
        mockMvc.perform(post(baseUrl + "/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "", "contactInfo": "john@mail.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("name cannot be empty"));
    }

    @Test
    void createSeller_nameIsTooLong_returns400() throws Exception {
        mockMvc.perform(post(baseUrl + "/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "AAAAAAAAAAAAAAAAAAAAAAAAA", "contactInfo": "john@mail.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("name cannot be longer than 16 characters"));
    }

    @Test
    void createSeller_contactInfoIsBlank_returns400() throws Exception {
        mockMvc.perform(post(baseUrl + "/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "John", "contactInfo": "  "}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("contactInfo cannot be empty"));
    }

    @Test
    void findAllSellers_returnsAll() throws Exception {
        String[] names = {"Alice", "Bob", "Charlie"};
        String[] contactInfos = {"alice@mail.com", "bob@mail.com", "charlie@mail.com"};
        int count = names.length;
        for (int i = 0; i < count; i++) {
            helper.createSeller(names[i], contactInfos[i]);
        }

        mockMvc.perform(get(baseUrl + "/sellers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sellers.length()").value(count))
                .andExpect(jsonPath("$.sellers[*].name", containsInAnyOrder(names)))
                .andExpect(jsonPath("$.sellers[*].contactInfo", containsInAnyOrder(contactInfos)))
                .andExpect(jsonPath("$.sellers[*].id").isNotEmpty())
                .andExpect(jsonPath("$.sellers[*].registrationDate").isNotEmpty());
    }

    @Test
    void findSellerById_success() throws Exception {
        String name = "Alice";
        String contactInfo = "alice@mail.com";
        int id = helper.createSeller(name, contactInfo);

        mockMvc.perform(get(baseUrl + "/sellers/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.contactInfo").value(contactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void findSellerById_notFound_returns404() throws Exception {
        mockMvc.perform(get(baseUrl + "/sellers/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("seller with id 999 not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details.length()").value(0));
    }

    @Test
    void updateSeller_success() throws Exception {
        String oldName = "Hank";
        String oldContactInfo = "schrader@mail.com";
        String newName = "Henry R";
        String newContactInfo = "henryrschrader@mail.com";
        int id = helper.createSeller(oldName, oldContactInfo);

        mockMvc.perform(put(baseUrl + "/sellers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "contactInfo": "%s"}
                                """.formatted(newName, newContactInfo)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.contactInfo").value(newContactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void updateSeller_nameIsBlank_returns400() throws Exception {
        int id = helper.createSeller("OldName", "old@mail.com");

        mockMvc.perform(put(baseUrl + "/sellers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "   ", "contactInfo": "john@mail.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("name cannot be empty"));
    }

    @Test
    void updateSeller_nameIsTooLong_returns400() throws Exception {
        int id = helper.createSeller("OldName", "old@mail.com");

        mockMvc.perform(put(baseUrl + "/sellers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "AAAAAAAAAAAAAAAAAAAAAAA", "contactInfo": "john@mail.com"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("name cannot be longer than 16 characters"));
    }

    @Test
    void updateSeller_contactInfoIsBlank_returns400() throws Exception {
        int id = helper.createSeller("OldName", "old@mail.com");

        mockMvc.perform(put(baseUrl + "/sellers/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "newName", "contactInfo": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details[0]").value("contactInfo cannot be empty"));
    }

    @Test
    void updateSeller_notFound_returns404() throws Exception {
        mockMvc.perform(put(baseUrl + "/sellers/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "someName", "contactInfo": "someContactInfo"}
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("seller with id 999 not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.details.length()").value(0));
    }

    @Test
    void deleteSeller_success() throws Exception {
        int id = helper.createSeller("ToDelete", "del@mail.com");

        mockMvc.perform(delete(baseUrl + "/sellers/{id}", id))
                .andExpect(status().isOk());

        mockMvc.perform(get(baseUrl + "/sellers/{id}", id))
                .andExpect(status().isNotFound());

        mockMvc.perform(get(baseUrl + "/sellers"))
                .andExpect(jsonPath("$.sellers.length()").value(0));
    }
}
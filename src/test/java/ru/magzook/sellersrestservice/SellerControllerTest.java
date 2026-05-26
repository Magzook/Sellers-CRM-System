package ru.magzook.sellersrestservice;

import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SellerControllerTest extends BaseIntegrationTest {

    private static final String sellersUrl = baseUrl + "/sellers";

    @Test
    void createSeller_success() throws Exception {
        String name = "John";
        String contactInfo = "john@mail.com";
        performPost(sellersUrl, makeSellerBody(name, contactInfo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.contactInfo").value(contactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void createSeller_nameIsBlank_returns400() throws Exception {
        var response = performPost(sellersUrl, makeSellerBody("", "john@mail.com"));
        expectValidationFailed(response,"name cannot be empty");
    }

    @Test
    void createSeller_nameIsTooLong_returns400() throws Exception {
        var response = performPost(sellersUrl, makeSellerBody("AAAAAAAAAAAAAAAAAAAAAAAAA", "john@mail.com"));
        expectValidationFailed(response,"name cannot be longer than 16 characters");
    }

    @Test
    void createSeller_contactInfoIsBlank_returns400() throws Exception {
        var response = performPost(sellersUrl, makeSellerBody("John", "   "));
        expectValidationFailed(response,"contactInfo cannot be empty");
    }

    @Test
    void findAllSellers_returnsAll() throws Exception {
        String[] names = {"Alice", "Bob", "Charlie"};
        String[] contactInfos = {"alice@mail.com", "bob@mail.com", "charlie@mail.com"};
        int count = 3;
        for (int i = 0; i < count; i++) {
            helper.createSeller(names[i], contactInfos[i]);
        }
        performGet(sellersUrl)
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

        performGet(sellersUrl + "/" + id)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.contactInfo").value(contactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void findSellerById_notFound_returns404() throws Exception {
        int id = 999;
        var response = performGet(sellersUrl + "/" + id);
        expectEntityWithIdNotFound(response, "seller", id);
    }

    @Test
    void findSellerById_badType_returns400() throws Exception {
        String fakeId = "123451234512345123451234512345";
        var response = performGet(sellersUrl + "/" + fakeId);
        expectMethodArgumentTypeMismatch(
                response,
                "id",
                "int",
                fakeId);
    }

    @Test
    void updateSeller_success() throws Exception {
        String oldName = "Hank";
        String oldContactInfo = "schrader@mail.com";
        String newName = "Henry R";
        String newContactInfo = "henryrschrader@mail.com";
        int id = helper.createSeller(oldName, oldContactInfo);

        performPut(sellersUrl + "/" + id, makeSellerBody(newName, newContactInfo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.contactInfo").value(newContactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void updateSeller_nameIsBlank_returns400() throws Exception {
        int id = helper.createSeller("OldName", "old@mail.com");
        var response = performPut(sellersUrl + "/" + id, makeSellerBody("  ", "john@mail.com"));
        expectValidationFailed(response,"name cannot be empty");
    }

    @Test
    void updateSeller_nameIsTooLong_returns400() throws Exception {
        int id = helper.createSeller("OldName", "old@mail.com");
        var response = performPut(sellersUrl + "/" + id, makeSellerBody("AAAAAAAAAAAAAAAAA", "john@mail.com"));
        expectValidationFailed(response,"name cannot be longer than 16 characters");
    }

    @Test
    void updateSeller_contactInfoIsBlank_returns400() throws Exception {
        int id = helper.createSeller("OldName", "old@mail.com");
        var response = performPut(sellersUrl + "/" + id, makeSellerBody("newName", ""));
        expectValidationFailed(response,"contactInfo cannot be empty");
    }

    @Test
    void updateSeller_notFound_returns404() throws Exception {
        int id = 999;
        var response = performPut(sellersUrl + "/" + id, makeSellerBody("someName", "someContactInfo"));
        expectEntityWithIdNotFound(response, "seller", id);
    }

    @Test
    void deleteSeller_success() throws Exception {
        int id = helper.createSeller("ToDelete", "del@mail.com");

        performDelete(sellersUrl + "/" + id)
                .andExpect(status().isOk());

        var response = performGet(sellersUrl + "/" + id);
        expectEntityWithIdNotFound(response, "seller", id);

        performGet(sellersUrl)
                .andExpect(jsonPath("$.sellers.length()").value(0));
    }

    private static String makeSellerBody(String name, String contactInfo) {
        return """
               {"name": "%s", "contactInfo": "%s"}
               """.formatted(name, contactInfo);
    }
}
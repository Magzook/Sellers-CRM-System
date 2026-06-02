package ru.magzook.sellersrestservice.api_tests;

import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.SELLERS;
import static ru.magzook.sellersrestservice.api_tests.helpers.UrlConstructor.sellersSlashId;

public class SellerControllerTest extends BaseIntegrationTest {

    private static final String ERROR_NAME_EMPTY = "name cannot be empty";
    private static final String ERROR_NAME_TOO_LONG = "name cannot be longer than 16 characters";
    private static final String ERROR_CONTACT_INFO_EMPTY = "contactInfo cannot be empty";

    @Test
    void createSeller_success() throws Exception {
        String name = "John";
        String contactInfo = "john@mail.com";
        httpRequestHelper.post(SELLERS, bodyHelper.makeSellerBody(name, contactInfo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.contactInfo").value(contactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void createSeller_nameIsBlank_returns400() throws Exception {
        var response = httpRequestHelper.post(SELLERS, bodyHelper.makeSellerBody("", "john@mail.com"));
        expectHelper.expectValidationFailed(response, ERROR_NAME_EMPTY);
    }

    @Test
    void createSeller_nameIsTooLong_returns400() throws Exception {
        var response = httpRequestHelper.post(SELLERS, bodyHelper.makeSellerBody("AAAAAAAAAAAAAAAAAAAAAAAAA", "john@mail.com"));
        expectHelper.expectValidationFailed(response, ERROR_NAME_TOO_LONG);
    }

    @Test
    void createSeller_contactInfoIsBlank_returns400() throws Exception {
        var response = httpRequestHelper.post(SELLERS, bodyHelper.makeSellerBody("John", "   "));
        expectHelper.expectValidationFailed(response, ERROR_CONTACT_INFO_EMPTY);
    }

    @Test
    void findAllSellers_returnsAll() throws Exception {
        String[] names = {"Alice", "Bob", "Charlie"};
        String[] contactInfos = {"alice@mail.com", "bob@mail.com", "charlie@mail.com"};
        int count = 3;
        for (int i = 0; i < count; i++) {
            crudHelper.createSeller(names[i], contactInfos[i]);
        }
        httpRequestHelper.get(SELLERS)
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
        int id = crudHelper.createSeller(name, contactInfo);

        httpRequestHelper.get(sellersSlashId(id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.contactInfo").value(contactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void findSellerById_notFound_returns404() throws Exception {
        int id = 999;
        var response = httpRequestHelper.get(sellersSlashId(id));
        expectHelper.expectEntityWithIdNotFound(response, "seller", id);
    }

    @Test
    void findSellerById_badType_returns400() throws Exception {
        String fakeId = "123451234512345123451234512345";
        var response = httpRequestHelper.get(SELLERS + "/" + fakeId);
        expectHelper.expectMethodArgumentTypeMismatch(
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
        int id = crudHelper.createSeller(oldName, oldContactInfo);

        httpRequestHelper.put(sellersSlashId(id), bodyHelper.makeSellerBody(newName, newContactInfo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(newName))
                .andExpect(jsonPath("$.contactInfo").value(newContactInfo))
                .andExpect(jsonPath("$.registrationDate").isNotEmpty());
    }

    @Test
    void updateSeller_nameIsBlank_returns400() throws Exception {
        int id = crudHelper.createSeller("OldName", "old@mail.com");
        var response = httpRequestHelper.put(sellersSlashId(id), bodyHelper.makeSellerBody("  ", "john@mail.com"));
        expectHelper.expectValidationFailed(response, ERROR_NAME_EMPTY);
    }

    @Test
    void updateSeller_nameIsTooLong_returns400() throws Exception {
        int id = crudHelper.createSeller("OldName", "old@mail.com");
        var response = httpRequestHelper.put(sellersSlashId(id), bodyHelper.makeSellerBody("AAAAAAAAAAAAAAAAA", "john@mail.com"));
        expectHelper.expectValidationFailed(response, ERROR_NAME_TOO_LONG);
    }

    @Test
    void updateSeller_contactInfoIsBlank_returns400() throws Exception {
        int id = crudHelper.createSeller("OldName", "old@mail.com");
        var response = httpRequestHelper.put(sellersSlashId(id), bodyHelper.makeSellerBody("newName", ""));
        expectHelper.expectValidationFailed(response, ERROR_CONTACT_INFO_EMPTY);
    }

    @Test
    void updateSeller_notFound_returns404() throws Exception {
        int id = 999;
        var response = httpRequestHelper.put(sellersSlashId(id), bodyHelper.makeSellerBody("someName", "someContactInfo"));
        expectHelper.expectEntityWithIdNotFound(response, "seller", id);
    }

    @Test
    void deleteSeller_success() throws Exception {
        int id = crudHelper.createSeller("ToDelete", "del@mail.com");

        httpRequestHelper.delete(sellersSlashId(id))
                .andExpect(status().isOk());

        var response = httpRequestHelper.get(sellersSlashId(id));
        expectHelper.expectEntityWithIdNotFound(response, "seller", id);

        httpRequestHelper.get(SELLERS)
                .andExpect(jsonPath("$.sellers.length()").value(0));
    }
}
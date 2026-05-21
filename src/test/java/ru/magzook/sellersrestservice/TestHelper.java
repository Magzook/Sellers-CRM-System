package ru.magzook.sellersrestservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Component
public class TestHelper {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    public int createSeller(String name, String contactInfo) throws Exception {
        String response = mockMvc.perform(post("/api/v1/sellers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s", "contactInfo": "%s"}
                                """.formatted(name, contactInfo)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asInt();
    }

    public int createTransaction(int amount, String paymentType, int sellerId) throws Exception {
        String response = mockMvc.perform(post("/api/v1/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"amount": %d, "paymentType": "%s", "sellerId": %d}
                                """.formatted(amount, paymentType, sellerId)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        return objectMapper.readTree(response).get("id").asInt();
    }
}

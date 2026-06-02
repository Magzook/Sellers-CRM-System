package ru.magzook.sellersrestservice.api_tests.helpers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.magzook.sellersrestservice.dto.enums.PaymentType;
import ru.magzook.sellersrestservice.dto.request.CreateTransactionRequestDto;
import ru.magzook.sellersrestservice.dto.request.CreateUpdateSellerRequestDto;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

@Component
public class BodyHelper {

    @Autowired
    private ObjectMapper objectMapper;

    public String makeSellerBody(String name, String contactInfo) {
        var dto = new CreateUpdateSellerRequestDto(name, contactInfo);
        return objectMapper.writeValueAsString(dto);
    }

    public String makeTransactionBody(BigDecimal amount, PaymentType paymentType, int sellerId) {
        var dto = new CreateTransactionRequestDto(amount, paymentType, sellerId);
        return objectMapper.writeValueAsString(dto);
    }
}

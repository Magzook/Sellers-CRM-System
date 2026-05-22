package ru.magzook.sellersrestservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.magzook.sellersrestservice.dto.enums.TimePeriod;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalListDto;
import ru.magzook.sellersrestservice.service.AnalyticsService;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    @Autowired
    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/top-1-sellers")
    public SellerWithTotalListDto getTopSellers(@RequestParam TimePeriod period) {
        return analyticsService.findTopSeller(period);
    }

    @GetMapping("/sellers-below-threshold")
    public SellerWithTotalListDto getSellersBelowThreshold(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime from,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime to,
            @RequestParam BigDecimal threshold) {
        return analyticsService.findSellersWithTotalBelow(from, to, threshold);
    }
}
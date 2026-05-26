package ru.magzook.sellersrestservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.magzook.sellersrestservice.dto.enums.TimePeriod;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalListDto;
import ru.magzook.sellersrestservice.repository.AnalyticsRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnalyticsService {
    private final AnalyticsRepository analyticsRepository;

    @Autowired
    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public SellerWithTotalListDto findTopSeller(TimePeriod period) {
        LocalDateTime[] range = period.whenBeganAndEnds();
        List<SellerWithTotalDto> sellers = analyticsRepository.findTopSellersByPeriod(range[0], range[1]);
        return new SellerWithTotalListDto(sellers);
    }

    public SellerWithTotalListDto findSellersWithTotalBelow(
            LocalDateTime from,
            LocalDateTime to,
            BigDecimal threshold)
    {
        List<SellerWithTotalDto> sellersWithTotalDtos = analyticsRepository
                .findSellersWithTotalBelow(from, to, threshold);
        return new SellerWithTotalListDto(sellersWithTotalDtos);
    }
}

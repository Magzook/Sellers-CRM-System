package ru.magzook.sellersrestservice.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto;
import ru.magzook.sellersrestservice.entity.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
// we don't want any CRUD methods here, so we extend the base Repository interface
public interface AnalyticsRepository extends org.springframework.data.repository.Repository<Seller, Integer> {

    @Query("""
        SELECT new ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto(
            s.id, s.name, s.contactInfo, s.registrationDate, SUM(t.amount)
        )
        FROM Seller s
        JOIN s.transactions t
        WHERE t.transactionDate >= :from AND t.transactionDate < :to
        GROUP BY s.id, s.name, s.contactInfo, s.registrationDate
        HAVING SUM(t.amount) = (
            SELECT MAX(sub_total) FROM (
                SELECT SUM(t2.amount) AS sub_total
                FROM Transaction t2
                WHERE t2.transactionDate >= :from AND t2.transactionDate < :to
                GROUP BY t2.seller
            )
        )
        """)
    List<SellerWithTotalDto> findTopSellersByPeriod(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    @Query("""
        SELECT new ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto(
            s.id, s.name, s.contactInfo, s.registrationDate, SUM(t.amount)
        )
        FROM Seller s
        JOIN s.transactions t
        WHERE t.transactionDate >= :from AND t.transactionDate < :to
        GROUP BY s.id, s.name, s.contactInfo, s.registrationDate
        HAVING SUM(t.amount) < :threshold
        """)
    List<SellerWithTotalDto> findSellersWithTotalBelow(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            @Param("threshold") BigDecimal threshold
    );
}
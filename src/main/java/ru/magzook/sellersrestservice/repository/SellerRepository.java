package ru.magzook.sellersrestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto;
import ru.magzook.sellersrestservice.entity.Seller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SellerRepository extends JpaRepository<Seller, Integer> {

    @Query("SELECT s FROM Seller s JOIN FETCH s.transactions WHERE s.id = :id")
    Optional<Seller> findByIdFetchTransactions(@Param("id") int id);

    @Query("""
    SELECT new ru.magzook.sellersrestservice.dto.response.SellerWithTotalDto(
        s.id, s.name, s.contactInfo, s.registrationDate, SUM(t.amount)
    )
    FROM Seller s
    JOIN s.transactions t
    WHERE t.transactionDate >= :from AND t.transactionDate < :to
    GROUP BY s.id, s.name, s.contactInfo, s.registrationDate
    ORDER BY SUM(t.amount) DESC
    LIMIT 1
    """)
    Optional<SellerWithTotalDto> findTopSellerByPeriod(
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

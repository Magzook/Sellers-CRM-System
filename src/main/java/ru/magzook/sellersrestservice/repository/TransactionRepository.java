package ru.magzook.sellersrestservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.magzook.sellersrestservice.entity.Transaction;

import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t JOIN FETCH t.seller WHERE t.id = :id")
    Optional<Transaction> findByIdFetchSeller(@Param("id") int id);
}

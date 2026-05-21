package ru.magzook.sellersrestservice.entity;

import jakarta.persistence.*;
import ru.magzook.sellersrestservice.dto.response.SellerDto;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "sellers")
public class Seller {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name", nullable = false, length = 16)
    private String name;

    @Column(name = "contact_info", nullable = false)
    private String contactInfo;

    @Column(name = "registration_date", nullable = false)
    private LocalDateTime registrationDate;

    @OneToMany(mappedBy = "seller", cascade = CascadeType.ALL)
    private Set<Transaction> transactions;

    public Seller() {
    }

    public Seller(int id, String name, String contactInfo, LocalDateTime registrationDate) {
        this.id = id;
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
    }

    public Seller(String name, String contactInfo, LocalDateTime registrationDate) {
        this.name = name;
        this.contactInfo = contactInfo;
        this.registrationDate = registrationDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public LocalDateTime getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDateTime registrationDate) {
        this.registrationDate = registrationDate;
    }

    public Set<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Set<Transaction> transactions) {
        this.transactions = transactions;
    }

    public SellerDto toDto() {
        return new SellerDto(
                id,
                name,
                contactInfo,
                registrationDate
        );
    }
}

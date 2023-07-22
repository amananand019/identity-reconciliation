package com.amananand.IdentityReconciliation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Contact {
    @ToString.Include
    @Id
    @Column(nullable = false)
    private int id;

    @ToString.Include
    private String phoneNumber;

    @ToString.Include
    private String email;

    @ToString.Include
    private Integer linkedId;

    @ToString.Include
    @Column(nullable = false)
    private String linkPrecedence;

    @ToString.Include
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @ToString.Include
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ToString.Include
    private LocalDateTime deletedAt;
}

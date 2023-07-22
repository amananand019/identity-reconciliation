package com.amananand.IdentityReconciliation.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phone_Number")
    private String phoneNumber;

    @Column(name = "email")
    private String email;

    @Column(name = "linked_Id")
    private Integer linkedId;

    @Column(name = "link_Precedence")
    private String linkPrecedence;

    @Column(name = "created_At")
    private LocalDateTime createdAt;

    @Column(name = "updated_At")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_At")
    private LocalDateTime deletedAt;
}


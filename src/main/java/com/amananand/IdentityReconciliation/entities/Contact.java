package com.amananand.IdentityReconciliation.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "contact")
@Getter
@Setter
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
    private String createdAt;

    @Column(name = "updated_At")
    private String updatedAt;

    @Column(name = "deleted_At")
    private String deletedAt;

    public Contact(String phoneNumber, String email, Integer linkedId, String linkPrecedence, String createdAt, String updatedAt) {
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.linkedId = linkedId;
        this.linkPrecedence = linkPrecedence;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}


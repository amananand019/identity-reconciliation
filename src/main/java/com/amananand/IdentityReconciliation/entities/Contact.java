package com.amananand.IdentityReconciliation.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contact")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "link_Precedence", nullable = false)
    private LinkPrecedence linkPrecedence;

    @Column(name = "created_At")
    private String createdAt;

    @Column(name = "updated_At")
    private String updatedAt;

    @Column(name = "deleted_At")
    private String deletedAt;
}


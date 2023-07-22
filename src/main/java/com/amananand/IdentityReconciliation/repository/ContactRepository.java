package com.amananand.IdentityReconciliation.repository;

import com.amananand.IdentityReconciliation.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
}

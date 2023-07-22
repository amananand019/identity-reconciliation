package com.amananand.IdentityReconciliation.controller;

import com.amananand.IdentityReconciliation.dto.ContactRequest;
import com.amananand.IdentityReconciliation.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {
    private final ContactService contactService;

    @Autowired
    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping("/identify")
    private String identify(@RequestBody ContactRequest request){
        System.out.println(contactService.check());
        return "Working properly";
    }
}

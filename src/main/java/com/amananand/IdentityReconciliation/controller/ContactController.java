package com.amananand.IdentityReconciliation.controller;

import com.amananand.IdentityReconciliation.dto.ContactRequest;
import com.amananand.IdentityReconciliation.dto.ContactResponse;
import com.amananand.IdentityReconciliation.services.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    private ResponseEntity<ContactResponse> identify(@RequestBody ContactRequest request){
        ContactResponse response = contactService.identifyService(request.getEmail(), request.getPhoneNumber());

        if(response != null){
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}

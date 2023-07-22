package com.amananand.IdentityReconciliation.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ContactResponse {
    private int primaryContactId;
    private List<String> emails;
    private List<String> phoneNumbers;
    private List<Integer> secondaryContactIds;

    public ContactResponse(int primaryContactId) {
        this.primaryContactId = primaryContactId;
        this.emails = new ArrayList<>();
        this.phoneNumbers = new ArrayList<>();
        this.secondaryContactIds = new ArrayList<>();
    }
}

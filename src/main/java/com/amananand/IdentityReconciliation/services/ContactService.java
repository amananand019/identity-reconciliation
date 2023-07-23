package com.amananand.IdentityReconciliation.services;

import com.amananand.IdentityReconciliation.dto.ContactResponse;
import com.amananand.IdentityReconciliation.entities.Contact;
import com.amananand.IdentityReconciliation.entities.LinkPrecedence;
import com.amananand.IdentityReconciliation.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public ContactResponse identifyService(String requestEmail, String requestPhoneNumber){
        if(!(requestPhoneNumber != null || requestEmail != null)){
            throw new RuntimeException("Both phoneNumber and Email are null");
        }

        List<Contact> contactList = contactRepository.findByEmailOrPhoneNumber(requestEmail, requestPhoneNumber);

        if(contactList.isEmpty()){
//            List empty means there is no contact present having either email or phoneNumber which is requested

            String currDateTime = getCurrentDateTime();
            Contact contact = new Contact(requestPhoneNumber, requestEmail, null, LinkPrecedence.PRIMARY.toString(), currDateTime, currDateTime);
            Contact savedContact = contactRepository.save(contact);

            ContactResponse contactResponse = new ContactResponse();
            contactResponse.setPrimaryContactId(savedContact.getId());

            if(requestEmail != null)
                contactResponse.getEmails().add(savedContact.getEmail());

            if(requestPhoneNumber != null)
                contactResponse.getPhoneNumbers().add(savedContact.getPhoneNumber());

            return contactResponse;
        } else {
            Set<Integer> primaryIdSet = findPrimaryContacts(contactList);
            Set<String> uniqueEmails = new HashSet<>();
            Set<String> uniquePhones = new HashSet<>();
            List<Integer> secondaryIds = new ArrayList<>();

            ContactResponse contactResponse = new ContactResponse();

            if(primaryIdSet.size() == 1){
                int primaryId = primaryIdSet.stream().findFirst().get();

                List<Contact> contacts = contactRepository.findByLinkedId(primaryId);

                for(Contact contact: contacts){
                    if(contact.getDeletedAt() != null)
                        continue;

                    secondaryIds.add(contact.getId());
                    String phone = contact.getPhoneNumber();
                    if(phone != null && phone.length() > 0)
                        uniquePhones.add(phone);

                    String email = contact.getEmail();
                    if(email != null && email.length() > 0)
                        uniqueEmails.add(email);
                }

                Optional<Contact> primaryContact = contactRepository.findById(primaryId);
                if(primaryContact.get().getPhoneNumber() != null && primaryContact.get().getPhoneNumber().length() > 0){
                    uniquePhones.add(primaryContact.get().getPhoneNumber());
                }

                if(primaryContact.get().getEmail() != null && primaryContact.get().getEmail().length() > 0){
                    uniqueEmails.add(primaryContact.get().getEmail());
                }

                contactResponse.setPrimaryContactId(primaryId);
                contactResponse.getSecondaryContactIds().addAll(secondaryIds);
                contactResponse.getPhoneNumbers().addAll(uniquePhones);
                contactResponse.getEmails().addAll(uniqueEmails);

                return contactResponse;
            } else {
//                  Two primary Ids present, means keep the contact as primary which was created first
//                  and update the other contact as secondary, Also update its secondary contacts with current primary id
                return null;
            }
        }
    }

    private HashSet<Integer> findPrimaryContacts(List<Contact> contacts){
        HashSet<Integer> primaryIdSet = new HashSet<>();
        for(Contact contact: contacts){
            Integer linkedId = contact.getLinkedId();
            if(linkedId != null){
                primaryIdSet.add(linkedId);
            } else {
                primaryIdSet.add(contact.getId());
            }
        }
        return primaryIdSet;
    }


    private String getCurrentDateTime(){
        String dateTimePattern = "dd-MM-yyyy HH:mm:ss.SSS XXX";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);
        return ZonedDateTime.now().format(formatter);
    }
}

package com.amananand.IdentityReconciliation.services;

import com.amananand.IdentityReconciliation.dto.ContactRequest;
import com.amananand.IdentityReconciliation.dto.ContactResponse;
import com.amananand.IdentityReconciliation.dto.IdentityPayloadDTO;
import com.amananand.IdentityReconciliation.entities.Contact;
import com.amananand.IdentityReconciliation.entities.LinkPrecedence;
import com.amananand.IdentityReconciliation.exceptions.InvalidInputException;
import com.amananand.IdentityReconciliation.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class ContactService {
    private final ContactRepository contactRepository;
    private final String dateTimePattern = "dd-MM-yyyy HH:mm:ss.SSS XXX";

    @Autowired
    public ContactService(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    public IdentityPayloadDTO identityService(ContactRequest contactRequest){
        ContactResponse contactResponse = identifyContactService(contactRequest.getEmail(), contactRequest.getPhoneNumber());
        return new IdentityPayloadDTO(contactResponse);
    }

    public ContactResponse identifyContactService(String requestEmail, String requestPhoneNumber){
        if(requestEmail != null && requestEmail.length() == 0)
            requestEmail = null;

        if(requestPhoneNumber != null && requestPhoneNumber.length() == 0)
            requestPhoneNumber = null;

        if(!(requestPhoneNumber != null || requestEmail != null))
            throw new InvalidInputException("Invalid Input: Both email and phone number cannot be empty at the same time");

        List<Contact> contactList = contactRepository.findByEmailOrPhoneNumber(requestEmail, requestPhoneNumber);

        if(contactList.isEmpty()){
//          List empty means there is no contact present having either email or phoneNumber which is requested
            Contact savedContact = savedContactToRepo(requestPhoneNumber, requestEmail, null, LinkPrecedence.PRIMARY);
            return createContactResponse(savedContact, new ArrayList<>());
        } else {
            Set<Integer> primaryIdSet = findPrimaryContacts(contactList);
            List<Contact> contacts = new ArrayList<>();

            for(Integer primary: primaryIdSet){
                contacts.add(contactRepository.findById(primary).orElse(null));
                contacts.addAll(contactRepository.findByLinkedId(primary));
            }

//            Sort contacts based on createdAt timestamp
            sortByCreatedAt(contacts);

            Contact primaryContact = contacts.get(0);
            if(requestEmail == null || requestPhoneNumber == null){
                return createContactResponse(primaryContact, contacts);
            }

//            Check if any new information available
            boolean phoneIsPresent = false, emailIsPresent = false;
            for(Contact c: contactList){
                if(requestPhoneNumber.equals(c.getPhoneNumber()))
                    phoneIsPresent = true;

                if(requestEmail.equals(c.getEmail()))
                    emailIsPresent = true;

//                if there is already a contact with same detail then no need to go further just return the response
                if(requestEmail.equals(c.getEmail()) && requestPhoneNumber.equals(c.getPhoneNumber())){
                    return createContactResponse(primaryContact, contacts);
                }
            }

            if(phoneIsPresent && emailIsPresent){
//              Both phone no. and email are present in it but in different contacts
//                So it need to update linkedId and LinkPrecedence if any other primary contact is present
                for(Contact contact: contacts){
                    if(!Objects.equals(contact.getId(), primaryContact.getId())
                            && !Objects.equals(contact.getLinkedId(), primaryContact.getId())){
                        contact.setLinkedId(primaryContact.getId());
                        contact.setLinkPrecedence(LinkPrecedence.SECONDARY);
                        contact.setUpdatedAt(getCurrentDateTime());
                        contactRepository.save(contact);
                    }
                }
            } else {
                Contact savedContact = savedContactToRepo(requestPhoneNumber, requestEmail, primaryContact.getId(), LinkPrecedence.SECONDARY);
                contacts.add(savedContact);
            }
            return createContactResponse(primaryContact, contacts);
        }
    }

    private ContactResponse createContactResponse(Contact primaryContact, List<Contact> otherContacts){
        List<String> emailsList = new ArrayList<>();
        List<String> phoneNumbersList = new ArrayList<>();
        List<Integer> secondaryContactIds = new ArrayList<>();

        emailsList.add(primaryContact.getEmail());
        phoneNumbersList.add(primaryContact.getPhoneNumber());

        HashSet<String> uniqueEmails = new HashSet<>();
        HashSet<String> uniquePhones = new HashSet<>();

        uniqueEmails.add(primaryContact.getEmail());
        uniquePhones.add(primaryContact.getPhoneNumber());

        for(Contact contact: otherContacts){
            if(!uniqueEmails.contains(contact.getEmail())){
                uniqueEmails.add(contact.getEmail());
                emailsList.add(contact.getEmail());
            }

            if (!uniquePhones.contains(contact.getPhoneNumber())) {
                uniquePhones.add(contact.getPhoneNumber());
                phoneNumbersList.add(contact.getPhoneNumber());
            }

            if (contact.getLinkPrecedence().equals(LinkPrecedence.SECONDARY)) {
                secondaryContactIds.add(contact.getId());
            }
        }

        return ContactResponse.builder()
                .primaryContactId(primaryContact.getId())
                .emails(emailsList)
                .phoneNumbers(phoneNumbersList)
                .secondaryContactIds(secondaryContactIds)
                .build();
    }

    private Contact savedContactToRepo(String phoneNumber, String email, Integer linkedId, LinkPrecedence linkPrecedence){
        String currTime = getCurrentDateTime();
        Contact newContact = Contact.builder()
                .phoneNumber(phoneNumber)
                .email(email)
                .linkPrecedence(linkPrecedence)
                .linkedId(linkedId)
                .createdAt(currTime)
                .updatedAt(currTime)
                .build();

        return contactRepository.save(newContact);
    }

    private Set<Integer> findPrimaryContacts(List<Contact> contacts){
        Set<Integer> primaryIdSet = new HashSet<>();
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateTimePattern);
        return ZonedDateTime.now().format(formatter);
    }

    private void sortByCreatedAt(List<Contact> contacts){
        SimpleDateFormat formatter = new SimpleDateFormat(dateTimePattern);
        contacts.sort((a, b) -> {
            try {
                Date dateTime1 = formatter.parse(a.getCreatedAt());
                Date dateTime2 = formatter.parse(b.getCreatedAt());
                return dateTime1.compareTo(dateTime2);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        });
    }
}

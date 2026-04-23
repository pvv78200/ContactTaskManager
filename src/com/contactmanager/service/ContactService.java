package com.contactmanager.service;

import com.contactmanager.dao.ContactDAO;
import com.contactmanager.model.Contact;
import com.contactmanager.util.InvalidDataException;
import java.util.List;

public class ContactService {

    private ContactDAO dao = new ContactDAO();

    public boolean addContact(Contact contact, int userId) throws InvalidDataException {

        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be empty!");
        }

        if (!contact.getPhone().matches("\\d{10}")) {
            throw new InvalidDataException("Phone number must be 10 digits!");
        }

        if (!contact.getEmail().contains("@")) {
            throw new InvalidDataException("Invalid Email Address!");
        }

        if (dao.contactExists(contact.getPhone(), userId)) {
            throw new InvalidDataException("Contact already exists for this user!");
        }

        return dao.addContact(contact, userId);
    }


    public List<Contact> getAllContacts(int userId) {
        return dao.getAllContacts(userId);
    }

    public boolean updateContact(Contact contact, int userId) throws InvalidDataException {

        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new InvalidDataException("Name cannot be empty!");
        }

        if (!contact.getPhone().matches("\\d{10}")) {
            throw new InvalidDataException("Phone number must be 10 digits!");
        }

        if (!contact.getEmail().contains("@")) {
            throw new InvalidDataException("Invalid Email Address!");
        }

        return dao.updateContact(contact, userId);
    }

    public boolean deleteContact(int contactId, int userId) {
        return dao.deleteContact(contactId, userId);
    }
}
package ch.zhaw.it.pm3.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class models an advertisement.
 */
public class Advertisement implements Serializable {
    @Serial
    private static final long serialVersionUID = 20L;

    private int id;
    private String title;
    private String description;
    private String surname;
    private String firstname;
    private String street;
    private int postalCode;
    private LocalDate date;
    private Skills requiredSkill;
    private int customerId;

    public Advertisement(DocumentInfo documentInfo, int id, int customerId) {
        this.id = id;
        this.title = documentInfo.title;
        this.description = documentInfo.description;
        this.surname = documentInfo.surname;
        this.firstname = documentInfo.firstname;
        this.street = documentInfo.street;
        this.postalCode = documentInfo.postalCode;
        this.date = documentInfo.date;
        this.requiredSkill = documentInfo.skill;
        this.customerId = customerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSurname() {
        return surname;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getStreet() {
        return street;
    }

    public int getPostalCode() {
        return postalCode;
    }

    public LocalDate getDate() {
        return date;
    }

    public Skills getRequiredSkill() {
        return requiredSkill;
    }

    public int getId() {
        return id;
    }

    public int getCustomerId() {
        return customerId;
    }

    @Override
    public String toString() {
        return title;
    }

}

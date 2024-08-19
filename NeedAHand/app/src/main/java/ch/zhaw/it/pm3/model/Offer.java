package ch.zhaw.it.pm3.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class models an offer.
 */
public class Offer implements Serializable {
    @Serial
    private static final long serialVersionUID = 30L;

    private String title;
    private String description;
    private String surname;
    private String firstname;
    private String mailAddress;
    private String street;
    private int postalCode;
    private double price;
    private int id;
    private int advertisementId;
    private int serviceProviderId;
    private DocumentInfo documentInfo;

    public Offer(DocumentInfo documentInfo, int id, int advertisementId, int serviceProviderId) {
        this.id = id;
        this.title = documentInfo.title;
        this.description = documentInfo.description;
        this.surname = documentInfo.surname;
        this.firstname = documentInfo.firstname;
        this.street = documentInfo.street;
        this.postalCode = documentInfo.postalCode;
        this.price = documentInfo.price;
        this.advertisementId = advertisementId;
        this.serviceProviderId = serviceProviderId;
        this.mailAddress = documentInfo.mailAddress;
        this.documentInfo = documentInfo;
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

    public Double getPrice() {
        return price;
    }

    public int getId() {
        return id;
    }

    public int getAdvertisementId() {
        return advertisementId;
    }

    public int getServiceProviderId() {
        return serviceProviderId;
    }

    public DocumentInfo getDocumentInfo() {
        return documentInfo;
    }

    /**
     * This method sets the title of the offer to a String.
     * @return title as a String
     */
    @Override
    public String toString() {
        return title;
    }
}

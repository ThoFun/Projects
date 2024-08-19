package ch.zhaw.it.pm3.model;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * This class provides the necessary information for the documents.
 */
public class DocumentInfo implements Serializable {
    public String title;
    public String description;
    public String surname;
    public String firstname;
    public String street;
    public int postalCode;
    public double price;
    public LocalDate date;
    public Skills skill;
    public String mailAddress;
}

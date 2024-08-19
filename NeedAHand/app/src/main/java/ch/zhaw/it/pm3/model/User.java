package ch.zhaw.it.pm3.model;

import java.io.Serial;
import java.io.Serializable;

/**
 * This class models a user.
 */
public class User implements Serializable {
    @Serial
    private static final long serialVersionUID = 10L;

    private int id;
    private String firstName;
    private String surname;
    private String mailAddress;
    private String password;
    private String street;
    private String city;
    private int postalCode;

    public User(UserInfo userInfo, int id) {
        this.id = id;
        this.firstName = userInfo.firstname;
        this.surname = userInfo.surname;
        this.mailAddress = userInfo.mailAddress;
        this.password = userInfo.password;
        this.street = userInfo.street;
        this.city = userInfo.city;
        this.postalCode = userInfo.postalCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getSurname() {
        return surname;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public String getPassword() {
        return password;
    }

    public String getStreet(){
        return street;
    }

    public int getId() {
        return id;
    }
    public int getPostalCode() {
        return postalCode;
    }

}

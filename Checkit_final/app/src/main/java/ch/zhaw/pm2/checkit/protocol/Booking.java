package ch.zhaw.pm2.checkit.protocol;

import ch.zhaw.pm2.checkit.server.FileBookingDatasource;
import ch.zhaw.pm2.checkit.server.Record;

import java.time.LocalDate;

/**
 * This class models a Booking
 */
public class Booking extends Record {
    private final String lastName;
    private final String mailAddress;
    private final LocalDate startTime;
    private final LocalDate endTime;
    private final String firstName;
    private boolean checkedIn = false;

    /**
     * Constructor
     *
     * @param firstName   of the hotel guest
     * @param lastName    of the hotel guest
     * @param mailAddress of the hotel guest
     * @param startTime   from when the hotel guest will stay
     * @param endTime     until the hotel guest will stay
     */
    public Booking(String firstName, String lastName, String mailAddress, LocalDate startTime, LocalDate endTime) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.mailAddress = mailAddress;
    }

    /**
     * Constructor
     *
     * @param id          booking ID
     * @param firstName   of the hotel guest
     * @param lastName    of the hotel guest
     * @param mailAddress of the hotel guest
     * @param startTime   from when the hotel guest will stay
     * @param endTime     until the hotel guest will stay
     */
    public Booking(long id, String firstName, String lastName, String mailAddress, LocalDate startTime, LocalDate endTime) {
        this(firstName, lastName, mailAddress, startTime, endTime);
        this.id = id;
    }

    /**
     * Constructor
     *
     * @param id          booking ID
     * @param firstName   of the hotel guest
     * @param lastName    of the hotel guest
     * @param mailAddress of the hotel guest
     * @param startTime   from when the hotel guest will stay
     * @param endTime     until the hotel guest will stay
     * @param checkedIn   true if guest is checked in else false
     */
    public Booking(long id, String firstName, String lastName, String mailAddress, LocalDate startTime, LocalDate endTime, boolean checkedIn) {
        this(id, firstName, lastName, mailAddress, startTime, endTime);
        this.checkedIn = checkedIn;
    }

    /**
     * Creates a Booking Object of a csv-based string
     *
     * @param line in csv file
     * @return new created Booking object
     */
    public static Booking fromString(String line) {
        String[] details = line.split(FileBookingDatasource.DELIMITER);
        return new Booking(Long.valueOf(details[0]), details[1], details[2], details[3], LocalDate.parse(details[4]), LocalDate.parse(details[5]), Boolean.valueOf(details[6]));
    }

    public void setCheckedIn() {
        checkedIn = true;
    }

    public void setCheckedInState(boolean newValue) {
        checkedIn = newValue;
    }

    public boolean isCheckedIn() {
        return checkedIn;
    }

    public LocalDate getStartTime() {
        return startTime;
    }

    public LocalDate getEndTime() {
        return endTime;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getMailAddress() {
        return mailAddress;
    }

    public String getLastName() {
        return lastName;
    }

    /**
     * Checks if the booking credentials are valid
     *
     * @return true, if the booking is valid
     */
    public boolean isValid() {
        return !firstName.isEmpty() && !lastName.isEmpty() && Mail.validateAddress(mailAddress) && startTime.isBefore(endTime);
    }

    /**
     * Sets the booking to a String for the CSV file
     *
     * @return a String for the CSV file
     */
    public String toCSV() {
        return String.join(FileBookingDatasource.DELIMITER,
                String.valueOf(id),
                firstName,
                lastName,
                mailAddress,
                startTime.toString(),
                endTime.toString(),
                Boolean.toString(checkedIn),
                "\n");
    }
}

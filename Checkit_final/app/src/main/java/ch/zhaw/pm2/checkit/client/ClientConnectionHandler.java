package ch.zhaw.pm2.checkit.client;

import ch.zhaw.pm2.checkit.protocol.Booking;
import ch.zhaw.pm2.checkit.server.Server;

import java.time.LocalDate;
import java.util.Map;

/**
 * Responsible for the client side connection of the application.
 */
public class ClientConnectionHandler {
    /**
     * Forwards the bookingID to the server and let him try to checkin
     *
     * @param bookingCode ID of the bookingCode
     * @return true if check-in is successful, else false
     */
    public boolean checkIn(long bookingCode) {
        return Server.checkIn(bookingCode);
    }

    /**
     * Forwards the bookingID to the server and let him try to checkout
     *
     * @param bookingCode ID of the bookingCode
     * @return true if check-out is successful, else false
     */
    public boolean checkOut(long bookingCode) {
        return Server.checkOut(bookingCode);
    }

    /**
     * Forwards the new booking to the server where it should be saved
     *
     * @param newBooking of new hotel guest
     * @return true if booking is added successful, else false
     */
    public boolean addNewBooking(Booking newBooking) {
        return Server.addNewBooking(newBooking);
    }

    public Map<LocalDate, LocalDate> getFreeTimeSlots(LocalDate from, LocalDate till, int amountOfNights) {
        return Server.getFreeTimeSlots(from, till, amountOfNights);
    }
}

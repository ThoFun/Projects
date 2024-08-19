package ch.zhaw.pm2.checkit.client;

import ch.zhaw.pm2.checkit.protocol.Booking;

import java.time.LocalDate;
import java.util.Map;

/**
 * Provides all kind of methods to work with bookings
 */
public class BookingManager {
    private final ClientConnectionHandler connectionHandler;

    /**
     * Constructor
     *
     * @param connectionHandler to be user by the Booking Manager
     */
    public BookingManager(ClientConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    /**
     * Tries to checkin the booking
     *
     * @param bookingCode ID of the bookingCode
     * @return true if check-in is successful, else false
     */
    public boolean checkIn(long bookingCode) {
        return connectionHandler.checkIn(bookingCode);
    }

    /**
     * Tries the check-out
     *
     * @param bookingCode ID of the bookingCode
     * @return true if check-out is successful, else false
     */
    public boolean checkOut(long bookingCode) {
        return connectionHandler.checkOut(bookingCode);
    }

    /**
     * Tries to safe the booking
     *
     * @param booking new created booking
     * @return true if booking is successful, else false
     */
    public boolean safeBooking(Booking booking) {
        return connectionHandler.addNewBooking(booking);
    }

    public Map<LocalDate, LocalDate> getAvailableTimeSlots(int amountOfNights) {
        return connectionHandler.getFreeTimeSlots(LocalDate.now(), LocalDate.now().plusYears(3), amountOfNights);
    }

}

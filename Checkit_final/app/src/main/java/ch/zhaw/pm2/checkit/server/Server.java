package ch.zhaw.pm2.checkit.server;

import ch.zhaw.pm2.checkit.protocol.Booking;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The Server checks the bookings
 */
public class Server {
    private static final String BOOKING_DB = "db/booking-data.csv";
    private static BookingDatasource dataSource;

    static {
        try {
            dataSource = new FileBookingDatasource(BOOKING_DB);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Only for test purposes
     *
     * @param bookingDatasource
     */
    public static void setDataSource(BookingDatasource bookingDatasource) {
        dataSource = bookingDatasource;
    }

    /**
     * Receives a booking code and tries to checkIn the corresponding booking
     *
     * @param bookingCode, the id of the booking
     */
    public static boolean checkIn(long bookingCode) {
        try {
            return dataSource.checkIn(bookingCode);
        } catch (RecordNotFoundException | IOException e) {
            return false;
        }
    }

    /**
     * Receives a booking code and checksOut the corresponding booking
     *
     * @param bookingCode, the id of the booking
     */
    public static boolean checkOut(long bookingCode) {
        try {
            return dataSource.checkOut(bookingCode);
        } catch (RecordNotFoundException | IOException e) {
            return false;
        }
    }

    /**
     * Returns true if a new booking, with the specified range, can be created.
     * Therefore, prevents overbooking.
     *
     * @param newBooking provides new booking of the hotel guest
     * @return true if dates are not booked, else false
     */
    public static boolean addNewBooking(Booking newBooking) {

        //Prevent overbooking
        Map<LocalDate, LocalDate> startToEndTime = new HashMap<>();
        LocalDate startTime = newBooking.getStartTime();
        LocalDate endTime = newBooking.getEndTime();
        startToEndTime.put(startTime, endTime);
        int amountOfNights = startTime.compareTo(endTime);
        if (!startToEndTime.equals(getFreeTimeSlots(startTime, endTime, amountOfNights))) return false;

        try {
            dataSource.insert(newBooking);
        } catch (IOException | RecordAlreadyExistsException e) {
            return false;
        }
        return true;
    }

    /**
     * Returns a list with available dates, based on amount of nights
     * From today till the next booking is also added for userExperience
     *
     * @param from
     * @param till
     * @param amountOfNights
     * @return A map, the key says from and the value till when it's available
     */
    public static Map<LocalDate, LocalDate> getFreeTimeSlots(LocalDate from, LocalDate till, int amountOfNights) {
        List<Booking> dateSortedBookings = new ArrayList<>();
        Map<LocalDate, LocalDate> dates = new HashMap<>();
        try {
            Stream<Booking> stream = dataSource.findAll().stream().sorted(Comparator.comparing(Booking::getStartTime));
            dateSortedBookings = stream.filter(b -> b.getStartTime().isBefore(till) && b.getEndTime().isAfter(from)).collect(Collectors.toList());
        } catch (IOException e) {
            return dates;
        }

        if (dateSortedBookings.isEmpty()) {
            dates.put(from, till);
        } else if (dateSortedBookings.size() < 2) {
            dates.put(from, dateSortedBookings.get(0).getStartTime());
            dates.put(dateSortedBookings.get(0).getEndTime(), till);
        } else {
            if (dateSortedBookings.get(0).getStartTime().isAfter(from))
                dates.put(from, dateSortedBookings.get(0).getStartTime());
            for (int i = 1; i < dateSortedBookings.size(); i++) {
                Booking currentBooking = dateSortedBookings.get(i);
                Booking earlierBooking = dateSortedBookings.get(i - 1);
                if (earlierBooking.getEndTime().isBefore(currentBooking.getStartTime().minusDays(amountOfNights - 1))) {
                    dates.put(earlierBooking.getEndTime(), currentBooking.getStartTime());
                }
            }
            dates.put(dateSortedBookings.get(dateSortedBookings.size() - 1).getEndTime(), till);
        }

        return dates;
    }
}

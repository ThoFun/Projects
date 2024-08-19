package ch.zhaw.pm2.checkit.server;

import ch.zhaw.pm2.checkit.protocol.Booking;

import java.io.IOException;

public interface BookingDatasource extends Datasource<Booking> {
    /**
     * Set the booking as checked in if the booking is valid.
     * The starttime of the booking needs to be in the present or past. The endtime needs to be in the future.
     *
     * @param bookingCode the id of the booking which wants to checkIn
     * @return true, if the check in was successful
     * @throws RecordNotFoundException if the bookingCode doesn't allocate to a booking
     */
    boolean checkIn(long bookingCode) throws RecordNotFoundException, IOException;

    /**
     * Set the booking as checked out if the booking is valid.
     *
     * @param bookingCode the id of the booking which wants to checkOut
     * @return true, if the check out was successful
     * @throws RecordNotFoundException if the bookingCode doesn't allocate to a booking
     */
    boolean checkOut(long bookingCode) throws RecordNotFoundException, IOException;
}

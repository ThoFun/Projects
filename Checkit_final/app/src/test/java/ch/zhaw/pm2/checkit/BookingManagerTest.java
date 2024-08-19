package ch.zhaw.pm2.checkit;

import ch.zhaw.pm2.checkit.client.BookingManager;
import ch.zhaw.pm2.checkit.client.ClientConnectionHandler;
import ch.zhaw.pm2.checkit.protocol.Booking;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.io.IOException;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class BookingManagerTest {
    BookingManager bookingManager;
    @Mock ClientConnectionHandler mockedConnectionHandler;
    @Mock Booking booking;

    @BeforeEach
    void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        bookingManager = new BookingManager(mockedConnectionHandler);
   }

    @Test
    void checkInTest() {
        bookingManager.checkIn(1L);
        verify(mockedConnectionHandler, times(1)).checkIn(anyLong());
    }

    @Test
    void checkOutTest() {
        bookingManager.checkOut(1L);
        verify(mockedConnectionHandler, times(1)).checkOut(anyLong());
    }

    @Test
    void safeBookingTest() {
        bookingManager.safeBooking(booking);
        verify(mockedConnectionHandler, times(1)).addNewBooking(any(Booking.class));
    }

    @Test
    void getAvailableTimeSlotsTest() {
        bookingManager.getAvailableTimeSlots(3);
        verify(mockedConnectionHandler, times(1)).getFreeTimeSlots(any(LocalDate.class), any(LocalDate.class), anyInt());
    }
}

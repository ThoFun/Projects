package ch.zhaw.pm2.checkit;

import ch.zhaw.pm2.checkit.client.BookingManager;
import ch.zhaw.pm2.checkit.client.ClientConnectionHandler;
import ch.zhaw.pm2.checkit.protocol.Booking;
import ch.zhaw.pm2.checkit.server.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.*;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Retention;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static ch.zhaw.pm2.checkit.server.FileBookingDatasource.CHARSET;

class ServerTest {
    @Mock FileBookingDatasource mockedDatasource;
    @Mock Booking booking;
    private int defaultAmountOfNights = 4;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Server.setDataSource(mockedDatasource);
    }

    @Test
    void checkInTest() throws RecordNotFoundException, IOException {
        Server.checkIn(1L);
        verify(mockedDatasource, times(1)).checkIn(anyLong());
    }

    @Test
    void checkOutTest() throws RecordNotFoundException, IOException {
        Server.checkOut(1L);
        verify(mockedDatasource, times(1)).checkOut(anyLong());
    }

    @Test
    void addNewBookingTest() throws IOException, RecordAlreadyExistsException {
        Booking testBooking = new Booking("Florian", "Tanner", "muster@mail.ch", LocalDate.now(), LocalDate.now().plusDays(3));
        Server.addNewBooking(testBooking);
        verify(mockedDatasource, times(1)).insert(any(Booking.class));
    }

    @Test
    void getAvailableTimeSlotsTest() throws IOException {
        Map<LocalDate, LocalDate> expectedOutput = new HashMap<>();
        int amountOfYearsToBeSearched = 2;

        LocalDate today = LocalDate.now();
        expectedOutput.put(today.plusDays(12), today.plusDays(20));
        expectedOutput.put(today.plusDays(25), today.plusYears(amountOfYearsToBeSearched));

        Booking testBooking1 = new Booking("Florian", "Tanner", "muster@mail.ch", today, today.plusDays(3));
        Booking testBooking2 = new Booking("Karin", "Renner", "muster2@mail.ch", today.plusDays(5), today.plusDays(12));
        Booking testBooking3 = new Booking("Frank", "Lampard", "muster3@mail.ch", today.plusDays(20), today.plusDays(25));

        when(mockedDatasource.findAll()).thenReturn(List.of(testBooking1, testBooking2, testBooking3));
        Map<LocalDate,LocalDate> generatedOutput = Server.getFreeTimeSlots(LocalDate.now(), LocalDate.now().plusYears(amountOfYearsToBeSearched), defaultAmountOfNights);

        assertEquals(expectedOutput, generatedOutput);
    }


    /**
     * The edges are e.g. i want to book for 3 nights and it fit's perfecty or it's one night too much
     * @throws IOException
     */
    @Test
    void getAvailableTimeSlotsEdgeCasesTest() throws IOException {
        Map<LocalDate, LocalDate> expectedOutput = new HashMap<>();
        int amountOfYearsToBeSearched = 2;

        LocalDate today = LocalDate.now();
        expectedOutput.put(today.plusDays(defaultAmountOfNights), today.plusDays(defaultAmountOfNights + defaultAmountOfNights));
        expectedOutput.put(today.plusDays(25), today.plusYears(amountOfYearsToBeSearched));

        Booking testBooking1 = new Booking("Florian", "Tanner", "muster@mail.ch", today, today.plusDays(defaultAmountOfNights));
        Booking testBooking2 = new Booking("Karin", "Renner", "muster2@mail.ch", today.plusDays(defaultAmountOfNights + defaultAmountOfNights), today.plusDays(20));
        Booking testBooking3 = new Booking("Frank", "Lampard", "muster3@mail.ch", today.plusDays(20 + defaultAmountOfNights - 1), today.plusDays(25));

        when(mockedDatasource.findAll()).thenReturn(List.of(testBooking1, testBooking2, testBooking3));
        Map<LocalDate,LocalDate> generatedOutput = Server.getFreeTimeSlots(LocalDate.now(), LocalDate.now().plusYears(amountOfYearsToBeSearched), defaultAmountOfNights);

        assertEquals(expectedOutput, generatedOutput);
    }

    /**
     * The free slots from today till the next booking are returned and not dependent on the amount of nights, for better user experience
     * @throws IOException
     */
    @Test
    void getAvailableTimeSlotsFreeTodayCaseTest() throws IOException {
        Map<LocalDate, LocalDate> expectedOutput = new HashMap<>();
        int amountOfYearsToBeSearched = 2;
        int todayOffset = 2;

        LocalDate today = LocalDate.now();
        expectedOutput.put(today, today.plusDays(todayOffset));
        expectedOutput.put(today.plusDays(todayOffset+todayOffset), today.plusYears(amountOfYearsToBeSearched));

        Booking testBooking1 = new Booking("Florian", "Tanner", "muster@mail.ch", today.plusDays(todayOffset), today.plusDays(todayOffset + todayOffset));

        when(mockedDatasource.findAll()).thenReturn(List.of(testBooking1));
        Map<LocalDate,LocalDate> generatedOutput = Server.getFreeTimeSlots(LocalDate.now(), LocalDate.now().plusYears(amountOfYearsToBeSearched), defaultAmountOfNights);

        assertEquals(expectedOutput, generatedOutput);
    }

    /**
     * Tests the insert call with already reserved dates
     * @throws IOException
     */
    @Test
    void addAlreadyBookedNewBookingTest() throws IOException {
        Booking newBooking = new Booking(
                1,
                "Max",
                "Mustermann",
                "muster@gmail.com",
                LocalDate.now(),
                LocalDate.now().plusDays(defaultAmountOfNights),
                false
        );

        Booking testBooking1 = new Booking("Florian", "Tanner", "muster@mail.ch", LocalDate.now(), LocalDate.now().plusDays(defaultAmountOfNights));

        when(mockedDatasource.findAll()).thenReturn(List.of(testBooking1));

        assertFalse(() -> Server.addNewBooking(newBooking));
    }
}

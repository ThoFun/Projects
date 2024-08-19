package ch.zhaw.pm2.checkit;

import ch.zhaw.pm2.checkit.protocol.Booking;
import ch.zhaw.pm2.checkit.server.BookingDatasource;
import ch.zhaw.pm2.checkit.server.FileBookingDatasource;
import ch.zhaw.pm2.checkit.server.RecordAlreadyExistsException;
import ch.zhaw.pm2.checkit.server.RecordNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static ch.zhaw.pm2.checkit.server.FileBookingDatasource.CHARSET;
import static ch.zhaw.pm2.checkit.server.FileBookingDatasource.DELIMITER;

class FileBookingDatasourceTest {
    private static final long EXISTING_ID = 1;
    private static final long INEXISTENT_ID = 0;
    private static final int defaultAmountOfNights = 4;

    Path dbTemplatePath;    // path of template database
    Path dbPath;            // path of temporary test database

    BookingDatasource datasource = null; // datasource instance to test

    FileBookingDatasourceTest() {
        URL dbTemplateUrl = FileBookingDatasourceTest.class.getClassLoader().getResource("db");
        Objects.requireNonNull(dbTemplateUrl, "Test database directory not found");
        String dbDir = new File(dbTemplateUrl.getPath()).getAbsolutePath();  // required for Windows to remove leading '/'
        String dbDirRaw = URLDecoder.decode(dbDir, CHARSET);  // replace urlencoded characters, e.g. %20 -> " "
        dbTemplatePath = Path.of(dbDirRaw, "test-data-template.csv");
        dbPath = Path.of(dbDirRaw, "test-data.csv");
    }

    @BeforeEach
    void setUp() throws IOException {
        // initialize test database file
        Files.copy(dbTemplatePath, dbPath);
        // setup datasource
        datasource = new FileBookingDatasource(dbPath.toString());
    }

    @AfterEach
    void tearDown() throws IOException {
        // cleanup test database file
        Files.deleteIfExists(dbPath);
    }


    @Test
    void insert() throws IOException, RecordAlreadyExistsException {
        Booking testBooking = new Booking("Florian", "Tanner", "muster@mail.ch", LocalDate.now(), LocalDate.now().plusDays(defaultAmountOfNights));
        assertEquals(-1, testBooking.getId(), "New booking must have Id -1");
        datasource.insert(testBooking);
        assertNotEquals(-1, testBooking.getId(), "Insert must set new id to booking");
        assertTrue(testBooking.getId() > 14, "Id must be larger than last existing");
        assertEquals(15, testBooking.getId(), "Id must be 1 larger than current highest");
        try {
            String insertedLine = readLineNo(4);
            assertNotNull(insertedLine, "Inserted line does not exist");
            assertEquals(bookingToCsvLine(testBooking), insertedLine);
        } catch (IOException e) {
            fail("Failed reading inserted booking record");
        }
    }

    @Test
    void insertExisting() {
        Booking testBooking = datasource.findById(EXISTING_ID);
        assertThrows(RecordAlreadyExistsException.class, () -> datasource.insert(testBooking));
        bookingEquals(testBooking, datasource.findById(testBooking.getId()));
    }

    @Test
    void insertNull() {
        assertThrows(NullPointerException.class, () -> datasource.insert(null));
    }

    @Test
    void checkin() {
        /* This test gets the booking with the existing id and does the checkin,
         * however, checkins are only possible if the start time is in the present or past and the endtime in the future.
         * As a result, the test will fail from 16.05 till you adapt the test data or improve the test :)
         */
        Booking originalBooking = datasource.findById(EXISTING_ID);
        assumeTrue(originalBooking != null);
        assumeTrue(originalBooking.getId() == EXISTING_ID);
        assumeTrue(!originalBooking.isCheckedIn());

        Booking updatedBooking = new Booking(
                originalBooking.getId(),
                originalBooking.getFirstName(),
                originalBooking.getLastName(),
                originalBooking.getMailAddress(),
                originalBooking.getStartTime(),
                originalBooking.getEndTime(),
                true);

        try {
            datasource.checkIn(updatedBooking.getId());
        } catch (RecordNotFoundException | IOException e) {
            fail("Test record not found for update", e);
        }

        Booking readUpdatedBooking = datasource.findById(originalBooking.getId());
        assertNotNull(readUpdatedBooking, "updated booking not found");
        bookingEquals(updatedBooking, readUpdatedBooking);
    }


    @Test
    void checkinInexistent() {
        assertThrows(RecordNotFoundException.class, () -> datasource.checkIn(INEXISTENT_ID));
    }

    @Test
    void findById() {
        Booking foundBooking = datasource.findById(EXISTING_ID);
        Booking createdBooking = new Booking(
                EXISTING_ID,
                "Pietro",
                "Fenner",
                "pife@gmail.com",
                LocalDate.of(2022,5,12),
                LocalDate.of(2022,6,15),
                false
        );
        bookingEquals(foundBooking, createdBooking);
    }

    @Test
    void findByIdInexistent() {
        Booking foundBooking = datasource.findById(INEXISTENT_ID);
        assertNull(foundBooking, "Inexistent Id found: " + INEXISTENT_ID);
    }

    @Test
    void findAll() throws IOException {
        Collection<Booking> bookings = datasource.findAll();
        assertNotNull(bookings, "Collection of bookings must not be null");
        assertEquals(countLines(), bookings.size(), "Number of records does not match number of found bookings");
        for (Booking booking : bookings) {
            assertNotNull(booking, "Found <null> booking in collection");
        }
    }

    /*
     * Helper methods
     */
    private void bookingEquals(Booking b1, Booking b2) {
        String message = "Bookings should remain the same";

        assertEquals(b1.getId(), b2.getId(), message);
        assertEquals(b1.getFirstName(), b2.getFirstName(), message);
        assertEquals(b1.getLastName(), b2.getLastName(), message);
        assertEquals(b1.getMailAddress(), b2.getMailAddress(), message);
        assertEquals(b1.getStartTime(), b2.getStartTime(), message);
        assertEquals(b1.getEndTime(), b2.getEndTime(), message);
        assertEquals(b1.isCheckedIn(), b2.isCheckedIn(), message);
    }

    private String readLineNo(int lineNo) throws IOException {
        try (Stream<String> lineStream = Files.lines(dbPath, CHARSET)) {
            return lineStream.skip(lineNo-1).findFirst().orElse(null);
        }
    }

    private String readLineWithId(long id) throws IOException {
        try (Stream<String> lineStream = Files.lines(dbPath, CHARSET)) {
            return lineStream.filter(line -> line.strip().startsWith(id+DELIMITER)).findFirst().orElse(null);
        }
    }

    private long countLines() {
        try (Stream<String> lineStream = Files.lines(dbPath, CHARSET)) {
            return lineStream.filter(Predicate.not(String::isBlank)).count();
        } catch (IOException e) {
            fail("Failed to count lines in db file", e);
        }
        return 0;
    }

    private String bookingToCsvLine(Booking booking) {
        assertNotNull(booking, "Booking must not be null");
        return String.join(FileBookingDatasource.DELIMITER,
                String.valueOf(booking.getId()),
                booking.getFirstName(),
                booking.getLastName(),
                booking.getMailAddress(),
                booking.getStartTime().toString(),
                booking.getEndTime().toString(),
                Boolean.toString(booking.isCheckedIn()),
                ""
        );
    }
}

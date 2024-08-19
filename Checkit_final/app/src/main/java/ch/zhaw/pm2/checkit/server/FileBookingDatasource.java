package ch.zhaw.pm2.checkit.server;

import ch.zhaw.pm2.checkit.protocol.Booking;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.OptionalLong;
import java.util.stream.Collectors;

/**
 * Implements the BookingDatasource Interface storing the data in
 * Character Separated Values (CSV) format, where each line consists of a record
 * whose fields are separated by the DELIMITER value ";"
 * See example file: db/booking-data.csv
 */
public class FileBookingDatasource implements BookingDatasource {
    // Delimiter to separate record fields on a line
    public static final String DELIMITER = ";";
    // Charset to use for file encoding.
    public static final Charset CHARSET = StandardCharsets.UTF_8;

    File db;


    /**
     * Creates the FileBookingDatasource object with the given file path as datafile.
     * Creates the file if it does not exist.
     * Also creates an empty temp file for write operations.
     *
     * @param filepath of the file to use as database file.
     * @throws IOException if accessing or creating the file fails
     */
    public FileBookingDatasource(String filepath) throws IOException {
        db = new File(filepath);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void insert(Booking booking) throws RecordAlreadyExistsException, IOException {
        if (!prepareBookingForInsert(booking)) throw new RecordAlreadyExistsException("Invalid booking object");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(db, CHARSET, true))) {
            appendToCSV(booking.toCSV(), bw);
        }
    }

    private boolean prepareBookingForInsert(Booking booking) throws IOException {
        if (booking.isNew()) booking.setId(getNewId());

        //check if the booking id isn't already set and return true if booking is valid
        return null == findById(booking.id);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void update(Booking booking) {
        // ToDo: Not Implemented in prototyp
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized void delete(Booking booking) throws IOException {
        List<Booking> bookings = findAll().stream().filter(b -> b.getId() != booking.getId()).collect(Collectors.toList());
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(db, CHARSET, false))) {
            for (Booking b : bookings) {
                appendToCSV(b.toCSV(), bw);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long count() {
        // ToDo: Not Implemented in prototyp
        return 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Booking findById(long id) {
        List<Booking> result;
        try (BufferedReader br = new BufferedReader(new FileReader(db, CHARSET))) {
            result = br.lines().filter(line -> Booking.fromString(line).getId() == id).map(Booking::fromString).collect(Collectors.toList());
        } catch (IOException e) {
            return null;
        }

        try {
            return result.get(0);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Collection<Booking> findAll() throws IOException {
        Collection<Booking> bookings = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(db, CHARSET))) {
            br.lines().forEach(line -> bookings.add(Booking.fromString(line)));
        }
        return bookings;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long getNewId() throws IOException {
        //TODO New ids shouldn't be depending on existing id's, for security reasons, not part of prototyp
        OptionalLong highestId = findAll().stream().mapToLong(Record::getId).max();
        if (!highestId.isEmpty()) {
            return highestId.getAsLong() + 1;
        } else return 1; //The first ID
    }

    private void appendToCSV(String toCSV, BufferedWriter bw) throws IOException {
        for (char c : toCSV.toCharArray()) {
            bw.append(c);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean checkIn(long bookingCode) throws RecordNotFoundException, IOException {
        return setCheckinState(bookingCode, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized boolean checkOut(long bookingCode) throws RecordNotFoundException, IOException {
        return setCheckinState(bookingCode, false);
    }

    private boolean setCheckinState(long bookingCode, boolean newState) throws IOException, RecordNotFoundException {
        Booking booking = findById(bookingCode);
        if (null == booking) throw new RecordNotFoundException("Can't find a associated");

        //early returns
        if (booking.isCheckedIn() == newState) return true;
        if (newState && booking.getStartTime().isAfter(LocalDate.now().minusDays(1)) &&
                booking.getEndTime().isAfter(LocalDate.now())
        ) return false;

        delete(booking);
        booking.setCheckedInState(newState);

        try {
            insert(booking);
        } catch (IOException e) {
            return false;
        } catch (RecordAlreadyExistsException e) {
            Booking bookingFromDB = findById(bookingCode);
            return bookingFromDB.isCheckedIn() == newState;
        }
        return true;
    }
}

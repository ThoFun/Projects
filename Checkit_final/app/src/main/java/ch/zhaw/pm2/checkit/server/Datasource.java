package ch.zhaw.pm2.checkit.server;

import ch.zhaw.pm2.checkit.protocol.Booking;

import java.io.IOException;
import java.util.Collection;

public interface Datasource<T extends Record> {
    /**
     * Insert a new record to the data source.
     * The id field of the record is ignored, and a new unique id has to be generated, which will be set in the record.
     * This id is used to identify the record in the dataset by the other methods (i.e. find, update or delete methods)
     *
     * @param record of type T to insert into the data set.
     */
    void insert(T record) throws IOException, RecordAlreadyExistsException;

    /**
     * Update the content of an existing record in the data set, which is identified by the unique identifier,
     * with the new values from the given record object.
     * If the identifier can not be found in the data set, an {@link RecordNotFoundException} is thrown.
     *
     * @param booking, the updated booking
     * @throws RecordNotFoundException if the record is not existing
     */
    void update(Booking booking) throws RecordNotFoundException;

    /**
     * Deletes the record, identified by the id of the given record from the data set.
     * All other fields of the record are ignored.
     * If the identifier can not be found in the data set, an {@link RecordNotFoundException} is thrown.
     *
     * @param record to be deleted
     * @throws RecordNotFoundException if the record is not existing
     */
    void delete(T record) throws RecordNotFoundException, IOException;

    /**
     * Returns the number of records in the data set
     *
     * @return number of records
     */
    long count();

    /**
     * Retrieves an instance of the record identified by the given id.
     * If the record can not be found, null is returned.
     * (better return type would be an {@link java.util.Optional} which is covered in part Functional Programming)
     * An empty result is not an error. Therefore, we do not throw an exception.
     *
     * @param id of the record to be retrieved
     * @return record of type T or null if not found
     */
    T findById(long id);

    /**
     * Retrieves all records of the data set.
     * If the dataset is empty an empty collection is returned.
     *
     * @return collection of all records of the data set
     */
    Collection<T> findAll() throws IOException;

    /**
     * Creates a new unique id
     *
     * @return long, which isn't taken in the database
     */
    long getNewId() throws IOException;
}

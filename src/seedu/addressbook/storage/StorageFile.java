package seedu.addressbook.storage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.data.person.Nric;
import seedu.addressbook.storage.jaxb.AdaptedAddressBook;

/**
 * Represents the file used to store address book data.
 */
public class StorageFile {

    /** Default file path used if the user doesn't provide the file name. */
    private static final String DEFAULT_STORAGE_FILEPATH = "policeRecords.txt";
    /* Note: Note the use of nested classes below.
     * More info https://docs.oracle.com/javase/tutorial/java/javaOO/nested.html
     */

    /**
     * Signals that the given file path does not fulfill the storage filepath constraints.
     */
    public static class InvalidStorageFilePathException extends IllegalValueException {
        InvalidStorageFilePathException(String message) {
            super(message);
        }
    }

    /**
     * Signals that some error has occured while trying to convert and read/write data between the application
     * and the storage file.
     */
    static class StorageOperationException extends Exception {
        StorageOperationException(String message) {
            super(message);
        }
    }

    private final Path path;

    private final JAXBContext jaxbContext;

    /**
     * @throws InvalidStorageFilePathException if the default path is invalid
     */
    public StorageFile() throws InvalidStorageFilePathException {
        this(DEFAULT_STORAGE_FILEPATH);
    }

    /**
     * @throws InvalidStorageFilePathException if the given file path is invalid
     */
    public StorageFile(String filePath) throws InvalidStorageFilePathException {
        try {
            jaxbContext = JAXBContext.newInstance(AdaptedAddressBook.class);
        } catch (JAXBException jaxbe) {
            throw new RuntimeException("jaxb initialisation error");
        }

        path = Paths.get(filePath);
        if (!isValidPath(path)) {
            throw new InvalidStorageFilePathException("Storage file should end with '.txt'");
        }
    }

    /**
     * Returns true if the given path is acceptable as a storage file.
     * The file path is considered acceptable if it ends with '.txt'
     */
    private static boolean isValidPath(Path filePath) {
        return filePath.toString().endsWith(".txt");
    }

    /**
     * Saves all data to this storage file.
     *
     * @throws StorageOperationException if there were errors converting and/or storing data to file.
     */
    public void save(AddressBook addressBook) throws StorageOperationException {

        /* Note: Note the 'try with resource' statement below.
         * More info: https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
         */
        try (final Writer fileWriter =
                     new BufferedWriter(new FileWriter(path.toFile()))) {

            final AdaptedAddressBook toSave = new AdaptedAddressBook(addressBook);
            final Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(toSave, fileWriter);

        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to file: " + path + " error: " + ioe.getMessage());
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error converting address book into storage format");
        }
    }

    /**
     * Loads data from this storage file.
     *
     * @throws StorageOperationException if there were errors reading and/or converting data from file.
     */
    public AddressBook load() throws StorageOperationException, IllegalValueException {
        try (final Reader fileReader =
                     new BufferedReader(new FileReader(path.toFile()))) {

            final Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            final AdaptedAddressBook loaded = (AdaptedAddressBook) unmarshaller.unmarshal(fileReader);
            // manual check for missing elements
            if (loaded.isAnyRequiredFieldMissing()) {
                throw new StorageOperationException("File data missing some elements");
            }
            return loaded.toModelType();

        /* Note: Here, we are using an exception to create the file if it is missing. However, we should minimize
         * using exceptions to facilitate normal paths of execution. If we consider the missing file as a 'normal'
         * situation (i.e. not truly exceptional) we should not use an exception to handle it.
         */

        // create empty file if not found
        } catch (FileNotFoundException fnfe) {
            //final AddressBook empty = new AddressBook();
            final AddressBook populated = populatedPoliceRecords();
            save(populated);
            return populated;


        // other errors
        } catch (IOException ioe) {
            throw new StorageOperationException("Error writing to file: " + path);
        } catch (JAXBException jaxbe) {
            throw new StorageOperationException("Error parsing file data format");
        } catch (IllegalValueException ive) {
            throw new StorageOperationException("File contains illegal data values; data type constraints not met");
        }
    }

    public String getPath() {
        return path.toString();
    }

    //@@iamputradanish

    /**
     * TODO: Add Javadoc comment
     * @return
     * @throws IllegalValueException
     */
    private AddressBook populatedPoliceRecords() throws IllegalValueException {
        return new AddressBook(new UniquePersonList(
                new Person(
                        new Name("AJ Styles"),
                        new Nric("s8012345a"),
                        new DateOfBirth("1980"),
                        new PostalCode("510246"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("theft"), new Offense("assault"))),
                new Person(
                        new Name("Becky Lynch"),
                        new Nric("s9611234c"),
                        new DateOfBirth("1996"),
                        new PostalCode("948372"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("riot"))),
                new Person(
                        new Name("Brock Lesnar"),
                        new Nric("s7512345a"),
                        new DateOfBirth("1975"),
                        new PostalCode("213456"),
                        new Status("clear"),
                        new Offense("none"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("Cesaro"),
                        new Nric("s8812341g"),
                        new DateOfBirth("1988"),
                        new PostalCode("402917"),
                        new Status("wanted"),
                        new Offense("murder"),
                        Set.of(new Offense("kidnap"))),
                new Person(
                        new Name("Pete Dunne"),
                        new Nric("s9443567h"),
                        new DateOfBirth("1994"),
                        new PostalCode("234567"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("piracy"), new Offense("assault"))),
                new Person(
                        new Name("Ronda Rousey"),
                        new Nric("s9154362c"),
                        new DateOfBirth("1991"),
                        new PostalCode("567342"),
                        new Status("clear"),
                        new Offense("none"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("Sheamus"),
                        new Nric("s8512356e"),
                        new DateOfBirth("1985"),
                        new PostalCode("908371"),
                        new Status("clear"),
                        new Offense("none"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("Seth Rollins"),
                        new Nric("s9012345k"),
                        new DateOfBirth("1990"),
                        new PostalCode("975241"),
                        new Status("wanted"),
                        new Offense("assault"),
                        Set.of(new Offense("riot"))),
                new Person(
                        new Name("Shinsuke Nakamura"),
                        new Nric("s8412345c"),
                        new DateOfBirth("1984"),
                        new PostalCode("738492"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("cheating"), new Offense("theft"))),
                new Person(
                        new Name("Adam Cole"),
                        new Nric("s9677843c"),
                        new DateOfBirth("1996"),
                        new PostalCode("545246"),
                        new Status("clear"),
                        new Offense("none"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("Aleister Black"),
                        new Nric("s9212386d"),
                        new DateOfBirth("1992"),
                        new PostalCode("125773"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("robbery"), new Offense("rape"))),
                new Person(
                        new Name("Alexa Bliss"),
                        new Nric("s9898325j"),
                        new DateOfBirth("1998"),
                        new PostalCode("876524"),
                        new Status("wanted"),
                        new Offense("rape"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("Apollo Crews"),
                        new Nric("s8988374e"),
                        new DateOfBirth("1989"),
                        new PostalCode("214563"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("riot"))),
                new Person(
                        new Name("Baron Corbin"),
                        new Nric("s9001235z"),
                        new DateOfBirth("1990"),
                        new PostalCode("468974"),
                        new Status("wanted"),
                        new Offense("murder"),
                        Set.of(new Offense("robbery"), new Offense("theft"))),
                new Person(
                        new Name("Randy Orton"),
                        new Nric("s8298760a"),
                        new DateOfBirth("1982"),
                        new PostalCode("568986"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("assault"))),
                new Person(
                        new Name("Paul Heyman"),
                        new Nric("s7656436d"),
                        new DateOfBirth("1976"),
                        new PostalCode("987764"),
                        new Status("clear"),
                        new Offense("none"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("Matt Hardy"),
                        new Nric("s8712392n"),
                        new DateOfBirth("1987"),
                        new PostalCode("928716"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("theft"))),
                new Person(
                        new Name("Jeff Hardy"),
                        new Nric("s9023472c"),
                        new DateOfBirth("1990"),
                        new PostalCode("029837"),
                        new Status("clear"),
                        new Offense("none"),
                        Set.of(new Offense("none"))),
                new Person(
                        new Name("John Cena"),
                        new Nric("s8017234a"),
                        new DateOfBirth("1980"),
                        new PostalCode("920175"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("robbery"), new Offense("assault"))),
                new Person(
                        new Name("Kevin Owens"),
                        new Nric("s9128391f"),
                        new DateOfBirth("1991"),
                        new PostalCode("928615"),
                        new Status("xc"),
                        new Offense("none"),
                        Set.of(new Offense("riot"), new Offense("theft")))
                        ));
    }
}

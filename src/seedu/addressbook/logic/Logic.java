package seedu.addressbook.logic;

import seedu.addressbook.commands.Command;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.data.AddressBook;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.person.*;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.parser.Parser;
import seedu.addressbook.readandwrite.ReaderAndWriter;
import seedu.addressbook.storage.StorageFile;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Represents the main Logic of the AddressBook.
 */
public class Logic {


    private StorageFile storage;
    private static AddressBook addressBook;
    private static ReaderAndWriter readerandwriter = new ReaderAndWriter();

    /** The list of person shown to the user most recently.  */
    private List<? extends ReadOnlyPerson> lastShownList = Collections.emptyList();

    public Logic() throws Exception{
        setStorage(initializeStorage());
        setAddressBook(storage.load());
        initializeTextFiles();
        populatePoliceRecords();
    }

    public static AddressBook getAddressBook() {
        return addressBook;
    }

    Logic(StorageFile storageFile, AddressBook addressBook){
        setStorage(storageFile);
        setAddressBook(addressBook);
    }

    void setStorage(StorageFile storage){
        this.storage = storage;
    }

    void setAddressBook(AddressBook addressBook){
        this.addressBook = addressBook;
    }

    /**
     * Creates the StorageFile object based on the user specified path (if any) or the default storage path.
     * @throws StorageFile.InvalidStorageFilePathException if the target file path is incorrect.
     */
    private StorageFile initializeStorage() throws StorageFile.InvalidStorageFilePathException {
        return new StorageFile();
    }

    public String getStorageFilePath() {
        return storage.getPath();
    }

    /**
     * Unmodifiable view of the current last shown list.
     */
    public List<ReadOnlyPerson> getLastShownList() {
        return Collections.unmodifiableList(lastShownList);
    }

    protected void setLastShownList(List<? extends ReadOnlyPerson> newList) {
        lastShownList = newList;
    }

    /**
     * Parses the user command, executes it, and returns the result.
     * @throws Exception if there was any problem during command execution.
     */
    public CommandResult execute(String userCommandText) throws Exception {
        Command command = new Parser().parseCommand(userCommandText);
        CommandResult result = execute(command);
        recordResult(result);
        return result;
    }

    /**
     * Executes the command, updates storage, and returns the result.
     *
     * @param command user command
     * @return result of the command
     * @throws Exception if there was any problem during command execution.
     */
    private CommandResult execute(Command command) throws Exception {
        command.setData(addressBook, lastShownList);
        CommandResult result = command.execute();
        storage.save(addressBook);
        return result;
    }

    /** Updates the {@link #lastShownList} if the result contains a list of Persons. */
    private void recordResult(CommandResult result) {
        final Optional<List<? extends ReadOnlyPerson>> personList = result.getRelevantPersons();
        if (personList.isPresent()) {
            lastShownList = personList.get();
        }
    }

    //@@iamputradanish
    /** Initializes password, env , message inboxes and screening history text files upon start up of program.*/
    private void initializeTextFiles(){
        try{
            File passwordFile = readerandwriter.fileToUse("passwordStorage.txt");
            BufferedReader br = readerandwriter.openReader(passwordFile);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse("passwordStorage.txt");
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("hqp -795402416\n" +
                    "po1 106852275\n" +
                    "po2 106852276\n" +
                    "po3 106852277\n" +
                    "po4 106852278\n" +
                    "po5 106852279\n");
            pw.flush();
            pw.close();
        }
        try{
            File notificationsFile = readerandwriter.fileToUse("notifications.txt");
            BufferedReader br = readerandwriter.openReader(notificationsFile);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse("notifications.txt");
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File envFile = readerandwriter.fileToUse("env");
            BufferedReader br = readerandwriter.openReader(envFile);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse("env");
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("AIzaSyBC7___BJc9QTTTzvZ9BHl2_7kx2FgrP8c");
            pw.flush();
            pw.close();
        }
        try{
            File directory = new File("inboxMessages");
            if(! directory.exists()){
                directory.mkdir();
            }
            File HQPFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_HQP_INBOX);
            BufferedReader br = readerandwriter.openReader(HQPFile);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_HQP_INBOX);
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File PO1File = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO1_INBOX);
            BufferedReader br = readerandwriter.openReader(PO1File);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO1_INBOX);
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File PO2File = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO2_INBOX);
            BufferedReader br = readerandwriter.openReader(PO2File);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO2_INBOX);
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File PO3File = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO3_INBOX);
            BufferedReader br = readerandwriter.openReader(PO3File);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO3_INBOX);
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File PO4File = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO4_INBOX);
            BufferedReader br = readerandwriter.openReader(PO4File);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO4_INBOX);
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File PO5File = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO5_INBOX);
            BufferedReader br = readerandwriter.openReader(PO5File);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse(MessageFilePaths.FILEPATH_PO5_INBOX);
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("");
            pw.flush();
            pw.close();
        }
        try{
            File screeningHistoryFile = readerandwriter.fileToUse("screeningHistory.txt");
            BufferedReader br = readerandwriter.openReader(screeningHistoryFile);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse("screeningHistory.txt");
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            pw.println("s1234567a 20/10/2018-16:11:45 Headquarters Personnel\n" +
                    "s1234567a 28/10/2018-03:02:00 Headquarters Personnel\n" +
                    "s1234567a 29/10/2018-12:45:13 Headquarters Personnel\n" +
                    "s1234567a 29/11/2018-22:02:00 Headquarters Personnel\n" +
                    "s1234567a 01/11/2018-22:02:00 Headquarters Personnel");
            pw.flush();
            pw.close();
        }
    }

    private void populatePoliceRecords() throws IllegalValueException {
        try{
            File screeningHistoryFile = readerandwriter.fileToUse("policeRecords.txt");
            BufferedReader br = readerandwriter.openReader(screeningHistoryFile);
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }catch(FileNotFoundException e){
            File makeFile = readerandwriter.fileToUse("policeRecords.txt");
            PrintWriter pw = null;
            try {
                pw = readerandwriter.openTempWriter(makeFile);
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
                    "<AddressBook>\n" +
                    "    <persons>\n" +
                    "        <name>John Doe</name>\n" +
                    "        <nric isPrivate=\"false\">s1234567a</nric>\n" +
                    "        <dateOfBirth isPrivate=\"false\">1996</dateOfBirth>\n" +
                    "        <postalCode isPrivate=\"false\">510246</postalCode>\n" +
                    "        <status isPrivate=\"false\">xc</status>\n" +
                    "        <wantedFor isPrivate=\"false\">none</wantedFor>\n" +
                    "        <tagged>drugs</tagged>\n" +
                    "        <tagged>theft</tagged>\n" +
                    "    </persons>\n" +
                    "    <persons>\n" +
                    "        <name>Jane Doe</name>\n" +
                    "        <nric isPrivate=\"false\">s1234567b</nric>\n" +
                    "        <dateOfBirth isPrivate=\"false\">1996</dateOfBirth>\n" +
                    "        <postalCode isPrivate=\"false\">510246</postalCode>\n" +
                    "        <status isPrivate=\"false\">xc</status>\n" +
                    "        <wantedFor isPrivate=\"false\">none</wantedFor>\n" +
                    "        <tagged>drugs</tagged>\n" +
                    "        <tagged>theft</tagged>\n" +
                    "    </persons>\n" +
                    "</AddressBook>";

            pw.println(xml);

            pw.flush();
            pw.close();
        }
    }
}

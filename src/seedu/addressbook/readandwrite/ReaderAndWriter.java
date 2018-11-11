package seedu.addressbook.readandwrite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//@@author iamputradanish

/**
 * TODO: Add Javadoc comment
 */
public class ReaderAndWriter {

    public File fileToUse (String pathName) {
        return new File(pathName);
    }

    public BufferedReader openReader (File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    public PrintWriter openTempWriter (File file) throws IOException {
        return new PrintWriter(new FileWriter(file));
    }

}

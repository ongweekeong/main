package seedu.addressbook.readandwrite;

import java.io.*;

//@@author iamputradanish
public class ReaderAndWriter {

    public File fileToUse (String pathName){
        return new File(pathName);
    }

    public BufferedReader openReader (File file) throws FileNotFoundException {
        return new BufferedReader(new FileReader(file));
    }

    public PrintWriter openTempWriter (File file) throws IOException {
        return new PrintWriter(new FileWriter(file));
    }

}
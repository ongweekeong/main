package seedu.addressbook;

import javafx.application.Application;
import javafx.application.Platform;

import javafx.stage.Stage;
import seedu.addressbook.inbox.MessageFilePaths;
import seedu.addressbook.logic.Logic;
import seedu.addressbook.readandwrite.ReaderAndWriter;
import seedu.addressbook.ui.Gui;
import seedu.addressbook.ui.Stoppable;

import java.io.*;

/**
 * Main entry point to the application.
 */
public class Main extends Application implements Stoppable{

    /** Version info of the program. */

    public static final String VERSION = "PRISM - Version 1.3";


    private Gui gui;

    private static ReaderAndWriter readerandwriter = new ReaderAndWriter();

    @Override
    public void start(Stage primaryStage) throws Exception{
        gui = new Gui(new Logic(), VERSION);
        gui.start(primaryStage, this);
        initializeTextFiles();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
        System.exit(0);
    }

    public static void main(String[] args) {
        launch(args);
    }

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
}



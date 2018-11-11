package seedu.addressbook.timeanddate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

//@@author iamputradanish

/**
 * TODO: Add Javadoc comment
 */
public class TimeAndDate {
    private static SimpleDateFormat timeStampFormatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss:SSS");
    private static SimpleDateFormat timeStampForMain = new SimpleDateFormat("dd/MM/yyyy HHmm");
    private static SimpleDateFormat timeStampFormatterForCheckCommand = new SimpleDateFormat("dd/MM/yyyy-HHmm");
    private Timestamp currentDat = new Timestamp(System.currentTimeMillis());
    private String outputMain = timeStampForMain.format(currentDat);
    private String outputDat = timeStampFormatter.format(currentDat);
    private String outputDatForCheckCommand = timeStampFormatterForCheckCommand.format(currentDat);

    public String outputDatHrs() {
        return outputDat + "hrs";
    }

    /**
     * TODO: Add Javadoc comment
     * @param theTime
     * @return
     */
    public static String outputDatHrs(Timestamp theTime) {
        String outputDat = timeStampFormatter.format(theTime);
        return outputDat + "hrs";
    }

    public String outputDatMainHrs() {
        return outputMain + "hrs";
    }

    /**
     * TODO: Add Javadoc comment
     * @param theTime
     * @return
     */
    public static String outputDatHrsForMain(Timestamp theTime) {
        String outputDat = timeStampForMain.format(theTime);
        return outputDat + "hrs";
    }

    public String getOutputDatHrsForCheckCommand() {
        return outputDatForCheckCommand + "hrs";
    }

}

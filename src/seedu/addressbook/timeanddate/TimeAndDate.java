package seedu.addressbook.timeanddate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

//@@author iamputradanish

/**
 * Class for generating timestamps in different formats (in string form)
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
     *
     * @param theTime
     * @return the timestamp in string
     */
    public static String outputDatHrs(Timestamp theTime) {
        String outputDat = timeStampFormatter.format(theTime);
        return outputDat + "hrs";
    }

    public String outputDatMainHrs() {
        return outputMain + "hrs";
    }

    /**
     * @param theTime
     * @return the timestamp in string
     */
    public static String outputDatHrsForMain(Timestamp theTime) {
        String outputDat = timeStampForMain.format(theTime);
        return outputDat + "hrs";
    }

    public String getOutputDatHrsForCheckCommand() {
        return outputDatForCheckCommand + "hrs";
    }

}

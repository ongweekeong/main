package seedu.addressbook.timeanddate;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

//@@author iamputradanish
public class TimeAndDate {
    private Timestamp currentDAT = new Timestamp(System.currentTimeMillis());
    private static SimpleDateFormat timeStampFormatter = new SimpleDateFormat("dd/MM/yyyy-HH:mm:ss:SSS");
    private static SimpleDateFormat timeStampForMain = new SimpleDateFormat("dd/MM/yyyy HHmm");
    private String outputMain = timeStampForMain.format(currentDAT);
    private static SimpleDateFormat timeStampFormatterForCheckCommand = new SimpleDateFormat("dd/MM/yyyy-HHmm");
    private String outputDAT = timeStampFormatter.format(currentDAT);
    private String outputDATForCheckCommand = timeStampFormatterForCheckCommand.format(currentDAT);

    public String outputDATHrs(){
        return outputDAT + "hrs";
    }
    public String outputDatMainHrs() { return outputMain + "hrs";}

    public static String outputDATHrs(Timestamp theTime){
        String outputDAT = timeStampFormatter.format(theTime);
        return outputDAT + "hrs";
    }

    public static String outputDATHrsForMain(Timestamp theTime){
        String outputDAT = timeStampForMain.format(theTime);
        return outputDAT + "hrs";
    }

    public String getOutputDAThrsForCheckCommand() {
        return outputDATForCheckCommand;
    }

}

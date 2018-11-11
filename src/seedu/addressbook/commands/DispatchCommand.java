//@@author andyrobert3
package seedu.addressbook.commands;

import java.io.IOException;
import java.util.ArrayList;

import org.javatuples.Pair;
import org.json.JSONException;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.common.Location;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.exception.PatrolResourceUnavailableException;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.inbox.NotificationWriter;

/**
 * Sends dispatch & backup message to relevant personnel
 */
public class DispatchCommand extends Command {
    public static final String COMMAND_WORD = "dispatch";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Headquarters Personnel would dispatch backup officer to requesting officer.\n\t"
            + "Parameters: dispatch [BACKUP_OFFICER_ID] [OFFENSE] [REQUESTER_OFFICER_ID] \n\t"
            + "Example: " + COMMAND_WORD
            + " PO1 gun PO3";

    private static String messageDispatchSuccess = "Dispatch for %s backup is successful.\n";

    private static String messageOfficerUnavailable = "Please choose another officer to send for backup.\n\t"
            + "Use 'checkstatus' command to see engaged/free officers.";

    private final NotificationWriter writeNotificationToBackupOfficer;
    private final NotificationWriter writeNotificationToRequester;

    private Location origin;
    private ArrayList<Location> destinationList;
    private String backupOfficer;
    private String requester;
    private String offense;

    /**
     * Convenience constructor using raw values.
     * Constructs for Writers to write to designated personnel files.
     */
    public DispatchCommand(String backupOfficer, String requester, String caseName) {
        writeNotificationToBackupOfficer = new NotificationWriter(backupOfficer);
        writeNotificationToRequester = new NotificationWriter(requester);

        this.offense = caseName;
        this.backupOfficer = backupOfficer;
        this.requester = requester;
        this.origin = PatrolResourceStatus.getLocation(backupOfficer);
        this.destinationList = new ArrayList<>();
    }

    private String generateStringMessage(String etaMessage, String patrolResourceId,
                                         String caseType, boolean isRequester) {
        return "Case type is: " + caseType + ", ETA " + etaMessage + ", Location of "
                + (isRequester ? "Requester: " : "Backup: ") + patrolResourceId + ", "
                + PatrolResourceStatus.getLocation(requester).getGoogleMapsUrl();
    }
    public static String getMessageDispatchSuccess() {
        return messageDispatchSuccess;
    }

    public static String getMessageOfficerUnavailable() {
        return messageOfficerUnavailable;
    }

    public static String getMessageBackupDispatchSame() {
        return "Backup resource & Requester cannot be the same officer %s!";
    }



    /**
     * TODO: Add Javadoc comment
     * @return
     */
    public CommandResult execute() {
        try {
            destinationList.add(PatrolResourceStatus.getLocation(requester));
            ArrayList<Pair<Integer, String>> etaList = origin.getEtaFrom(destinationList);

            Pair<Integer, String> etaPair = etaList.get(0);

            String dispatchStringMessage = generateStringMessage(etaPair.getValue1(), requester, this.offense, true);
            String requesterStringMessage = generateStringMessage(etaPair.getValue1(), backupOfficer,
                                                    this.offense, false);

            Msg dispatchMessage = new Msg(Offense.getPriority(offense), dispatchStringMessage,
                    PatrolResourceStatus.getLocation(requester), etaPair.getValue0());

            if (PatrolResourceStatus.getPatrolResource(backupOfficer).getValue2()) {
                throw new PatrolResourceUnavailableException(backupOfficer);
            }

            PatrolResourceStatus.setStatus(backupOfficer, true);

            Msg requesterMessage = new Msg(Offense.getPriority(offense), requesterStringMessage,
                    PatrolResourceStatus.getLocation(backupOfficer), etaPair.getValue0());

            writeNotificationToRequester.writeToFile(requesterMessage);
            writeNotificationToBackupOfficer.writeToFile(dispatchMessage);
        } catch (IOException ioe) {
            return new CommandResult(Messages.MESSAGE_INTERNET_NOT_AVAILABLE + "/"
                    + Messages.MESSAGE_SAVE_ERROR);
        } catch (IllegalValueException ioe) {
            return new CommandResult(String.format(Offense.MESSAGE_OFFENSE_INVALID + "\n"
                    + Offense.getListOfValidOffences(), this.offense));
        } catch (JSONException jse) {
            return new CommandResult(Messages.MESSAGE_JSON_PARSE_ERROR);
        } catch (PatrolResourceUnavailableException prue) {
            return new CommandResult(prue.getMessage() + "\n"
                    + messageOfficerUnavailable);
        }

        return new CommandResult(String.format(messageDispatchSuccess, requester));
    }
}

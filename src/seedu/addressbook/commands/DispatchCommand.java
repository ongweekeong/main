//@@author andyrobert3
package seedu.addressbook.commands;

import org.javatuples.Pair;
import org.json.JSONException;
import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.common.Location;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;
import seedu.addressbook.data.exception.PatrolResourceUnavailableException;
import seedu.addressbook.data.person.Offense;
import seedu.addressbook.inbox.Msg;
import seedu.addressbook.inbox.WriteNotification;

import java.io.IOException;
import java.util.ArrayList;

public class DispatchCommand extends Command {
    public static final String COMMAND_WORD = "dispatch";

    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n"
            + "Headquarters Personnel would dispatch backup officer to requesting officer.\n\t"
            + "Parameters: dispatch [BACKUP_OFFICER_ID] [OFFENSE] [REQUESTER_OFFICER_ID] \n\t"
            + "Example: " + COMMAND_WORD
            + " PO1 gun PO3";

    public static String MESSAGE_DISPATCH_SUCCESS = "Dispatch for %s backup is successful.\n";

    public static String MESSAGE_OFFICER_UNAVAILABLE = "Please choose another officer to send for backup.\n\t"
            + "Use 'checkstatus' command to see engaged/free officers.";

    public static String MESSAGE_BACKUP_DISPATCH_SAME = "Backup resource & Requester cannot be the same officer %s!";

    private final WriteNotification writeNotificationToBackupOfficer;
    private final WriteNotification writeNotificationToRequester;

    private Location origin;
    private ArrayList<Location> destinationList;
    private String backupOfficer;
    private String requester;
    private String offense;

    /**
     * Convenience constructor using raw values
     */
    public DispatchCommand(String backupOfficer, String requester, String caseName) {
        writeNotificationToBackupOfficer = new WriteNotification(backupOfficer);
        writeNotificationToRequester = new WriteNotification(requester);

        this.offense = caseName;
        this.backupOfficer = backupOfficer;
        this.requester = requester;
        this.origin = PatrolResourceStatus.getLocation(backupOfficer);
        this.destinationList = new ArrayList<>();
    }

    private String generateStringMessage(String etaMessage, String patrolResourceId, boolean isRequester) {
        return "ETA " + etaMessage + ", Location of " + (isRequester ? "Requester: " : "Backup: ")
                + patrolResourceId  + ", " + PatrolResourceStatus.getLocation(requester).getGoogleMapsURL();
    }

    public CommandResult execute() {

        try {
            destinationList.add(PatrolResourceStatus.getLocation(requester));
            ArrayList<Pair<Integer, String>> etaList = origin.getEtaFrom(destinationList);

            Pair<Integer, String> etaPair = etaList.get(0);

            String dispatchStringMessage = generateStringMessage(etaPair.getValue1(), requester, true);
            String requesterStringMessage = generateStringMessage(etaPair.getValue1(), backupOfficer, false);

            Msg dispatchMessage = new Msg(Offense.getPriority(offense), dispatchStringMessage,
                    PatrolResourceStatus.getLocation(requester), etaPair.getValue0());

            if (PatrolResourceStatus.getPatrolResource(backupOfficer).getValue2()) {
                throw new PatrolResourceUnavailableException(backupOfficer);
            }

            PatrolResourceStatus.setStatus(backupOfficer, true);

            Msg requesterMessage = new Msg(Offense.getPriority(offense), requesterStringMessage,
                    PatrolResourceStatus.getLocation(backupOfficer), etaPair.getValue0());

            //PatrolResourceStatus.setStatus(requester, true);
            writeNotificationToRequester.writeToFile(requesterMessage);
            writeNotificationToBackupOfficer.writeToFile(dispatchMessage);
        } catch (IOException ioe) {
            return new CommandResult(Messages.MESSAGE_INTERNET_NOT_AVAILABLE + "/" + Messages.MESSAGE_SAVE_ERROR);
        } catch (IllegalValueException ioe) {
            return new CommandResult(String.format(Offense.MESSAGE_OFFENSE_INVALID + "\n" + Offense.getListOfValidOffences(), this.offense));
        } catch (JSONException jse) {
            return new CommandResult(Messages.MESSAGE_JSON_PARSE_ERROR);
        } catch (PatrolResourceUnavailableException prue) {
            return new CommandResult(prue.getMessage() + "\n" + MESSAGE_OFFICER_UNAVAILABLE + "/" + " ");
        }

        return new CommandResult(String.format(MESSAGE_DISPATCH_SUCCESS, requester));
    }
}

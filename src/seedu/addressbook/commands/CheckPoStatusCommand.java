package seedu.addressbook.commands;

//@@author muhdharun
import java.util.ArrayList;
import java.util.List;

import org.javatuples.Triplet;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.common.Location;

/**
 * Returns a list of all POs and their current engagement status
 */

public class CheckPoStatusCommand extends Command {

    public static final String COMMAND_WORD = "checkstatus";
    public static final String MESSAGE_USAGE = COMMAND_WORD + ":\n" + "Gets current status of all POs \n\t"
            + "Example: " + COMMAND_WORD;

    @Override
    public CommandResult execute() {
        List<String> allPos = extractEngagementInformation(PatrolResourceStatus.getPatrolResourceStatus());

        return new CommandResult(getMessage(allPos));
    }

    /**
     * @param pos is the ArrayList of POs and their details of ID,Location and engagement status
     * @return a list of just POs and their respective engagement status
     */
    public static List<String> extractEngagementInformation(ArrayList<Triplet<String, Location, Boolean>> pos) {
        List<String> allPos = new ArrayList<>();
        for (Triplet<String, Location, Boolean> po : pos) {
            String poStatus = po.getValue0() + " " + po.getValue2();
            allPos.add(poStatus);
        }
        return allPos;
    }

}

//@@author andyrobert3
package seedu.addressbook;

import org.javatuples.Triplet;

import seedu.addressbook.common.Location;
import seedu.addressbook.common.Messages;
import seedu.addressbook.data.exception.IllegalValueException;

import java.util.ArrayList;

public class PatrolResourceStatus {
    public static final String HEADQUARTER_PERSONNEL_ID = "hqp";
    public static final String POLICE_OFFICER_1_ID = "po1";
    public static final String POLICE_OFFICER_2_ID = "po2";
    public static final String POLICE_OFFICER_3_ID = "po3";
    public static final String POLICE_OFFICER_4_ID = "po4";
    public static final String POLICE_OFFICER_5_ID = "po5";

    // Triplet<Police Officer ID, Location, isEngaged
    private static ArrayList<Triplet<String, Location, Boolean>> patrolResourceStatus = new ArrayList<>(){{
        add( new Triplet<>(HEADQUARTER_PERSONNEL_ID, new Location(1.294166, 103.770730), true) ); // NUS FASS
        add( new Triplet<>(POLICE_OFFICER_1_ID, new Location(1.306935, 103.790564), false) ); // Buona Vista
        add( new Triplet<>(POLICE_OFFICER_2_ID, new Location(1.346301, 103.682060), false) ); // NTU
        add( new Triplet<>(POLICE_OFFICER_3_ID, new Location(1.296057, 103.849865), false) ); // SMU
        add( new Triplet<>(POLICE_OFFICER_4_ID, new Location(1.340352, 103.962193), false) ); // SUTD
        add( new Triplet<>(POLICE_OFFICER_5_ID, new Location(1.329393, 103.776169), false) ); // SIM
    }};

    public static ArrayList<Triplet<String, Location, Boolean>> getPatrolResourceStatus() {
        return patrolResourceStatus;
    }

    public static void resetPatrolResourceStatus() {
        int index = 0;
        for (Triplet<String, Location, Boolean> policeOfficer : patrolResourceStatus) {
            if (policeOfficer.getValue0().equalsIgnoreCase(HEADQUARTER_PERSONNEL_ID)) {
                patrolResourceStatus.set(index, Triplet.with(policeOfficer.getValue0(),policeOfficer.getValue1(), true));
            } else {
                patrolResourceStatus.set(index, Triplet.with(policeOfficer.getValue0(),policeOfficer.getValue1(), false));
            }
            index++;
        }
    }

    public static Triplet<String, Location, Boolean> getPatrolResource(String patrolResource) {
        switch (patrolResource) {
            case HEADQUARTER_PERSONNEL_ID:
                return patrolResourceStatus.get(0);
            case POLICE_OFFICER_1_ID:
                return patrolResourceStatus.get(1);
            case POLICE_OFFICER_2_ID:
                return patrolResourceStatus.get(2);
            case POLICE_OFFICER_3_ID:
                return patrolResourceStatus.get(3);
            case POLICE_OFFICER_4_ID:
                return patrolResourceStatus.get(4);
            case POLICE_OFFICER_5_ID:
                return patrolResourceStatus.get(5);
            default:
                return patrolResourceStatus.get(0); // default for test cases
        }
    }

    // TODO: remove this duplicate code
    public static Location getLocation(String patrolResource) {
        switch (patrolResource) {
            case "hqp":
                return patrolResourceStatus.get(0).getValue1();
            case "po1":
                return patrolResourceStatus.get(1).getValue1();
            case "po2":
                return patrolResourceStatus.get(2).getValue1();
            case "po3":
                return patrolResourceStatus.get(3).getValue1();
            case "po4":
                return patrolResourceStatus.get(4).getValue1();
            case "po5":
                return patrolResourceStatus.get(5).getValue1();
            default:
                return patrolResourceStatus.get(0).getValue1(); // default for test cases
        }
    }

    /**
     * Sets engagement status of a selected police officer
     *
     * @param policeOfficerId
     * @param status
     * @throws IllegalValueException if policeOfficerId is not valid
     */
    public static void setStatus(String policeOfficerId, Boolean status) throws IllegalValueException {
        int index = 0;
        for (Triplet<String, Location, Boolean> policeOfficer : patrolResourceStatus) {
            if (policeOfficer.getValue0().equalsIgnoreCase(policeOfficerId)) {
                patrolResourceStatus.set(index,Triplet.with(policeOfficer.getValue0(),policeOfficer.getValue1(),status));
                return;
            }
            index++;
        }
        throw new IllegalValueException(Messages.MESSAGE_PO_NOT_FOUND);
    }

}

//@@author ongweekeong
package seedu.addressbook.inbox;


import seedu.addressbook.common.Location;
import seedu.addressbook.password.Password;
import seedu.addressbook.timeanddate.TimeAndDate;

import java.sql.Timestamp;

/**
 * Stores information on notifications that are sent from one user to another.
 * @params SenderID, Read Status, Priority, timestamp, message are compulsory fields.
 * @params location (x,y coordinates) and ETA are non-compulsory fields.
 */
public class Msg implements Comparable <Msg> {
    private String senderId = Password.getID();
    private String newMsg;
    private Priority priority;
    private Location location;
    private int eta = -1;

    private boolean isRead;
    boolean isLocationAvailable;
    private Timestamp time;
    private static final boolean MESSAGE_IS_READ = true;
    private static final boolean MESSAGE_IS_UNREAD = false;

    public enum Priority {
        HIGH(3),   // For messages that require HPQ intervention
        MED(2),    // For messages that only require PO back-up
        LOW(1);     // Messages that are FYI (e.g. Notifications to admin that details of subjects have changed

        private int priority;

        Priority (int priority) {
            this.priority = priority;
        }

        public int toInteger() {
            return priority;
        }

    }

    public Msg(){
        isLocationAvailable = false;
        isRead = MESSAGE_IS_UNREAD;
    }

    public Msg(Priority urgency, String message){
        isLocationAvailable = false;
        isRead = MESSAGE_IS_UNREAD;
        priority = urgency;
        time = new Timestamp(System.currentTimeMillis());
        newMsg = message;
    }

    public Msg(Priority urgency, String message, Location myLocale){ // Constructor for request backup message
        isLocationAvailable = true;
        isRead = MESSAGE_IS_UNREAD;
        priority = urgency;
        time = new Timestamp(System.currentTimeMillis());
        newMsg = message;
        location = myLocale;
    }

    // Constructor for request backup message
    public Msg(Priority urgency, String message, Location requesterLocale, int myEta){
        isLocationAvailable = true;
        priority = urgency;
        time = new Timestamp(System.currentTimeMillis());
        newMsg = message;
        location = requesterLocale;
        eta = myEta;
    }

    /**
     * Implements comparable so that messages can be sorted first by its read status, followed by the priority
     * and then the timestamp. Unread messages, higher priority messages and earlier messages should be sorted
     * ahead of other messages.
     * @param other
     * @return
     */
    @Override
    public int compareTo(Msg other) {
        int otherReadState = other.isRead? 1 : 0;
        int myReadState = isRead? 1 : 0;
        int compare = Integer.compare(myReadState, otherReadState);
        if(compare == 0) { // If same read status, compare priorities.
            compare = compareByPriority(other);
        }
        if(compare == 0){ // If priority is the same, compare by timestamp.
            compare = compareByTimestamp(other);
        }

        return compare;
    }

    private int compareByPriority(Msg other){
        return Integer.compare(other.getPriority().toInteger(), getPriority().toInteger());
    }

    private int compareByTimestamp(Msg other){
        return getTime().compareTo(other.getTime());
    }

    void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId(){
        return senderId;
    }

    public boolean hasBeenRead(){
        return isRead;
    }

    public void setReadStatus(boolean isRead){
        this.isRead = isRead;
    }

    public void setMsg(String msg){
        newMsg = msg;
    }

    void setPriority(Priority urgency){
        priority = urgency;
    }

    public Priority getPriority(){
        return priority;
    }

    public String getMsg(){
        return newMsg;
    }

    public void setLocation(Location place){
        location = place;
        isLocationAvailable = true;
    }

    public void setMsgAsRead(){
        isRead = MESSAGE_IS_READ;
    }

    double getLongitude(){
        return location.getLongitude();
    }

    double getLatitude() {
        return location.getLatitude();
    }

    int getEta(){
        return eta;
    }

    boolean hasEta(){
       return eta != -1;
    }

    public void setTime(Timestamp time){
        this.time = time;
    }

    public Timestamp getTime(){
        return time;
    }

    public String getTimeString() {
        return TimeAndDate.outputDATHrs(time);
    }

}

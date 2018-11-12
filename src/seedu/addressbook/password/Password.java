package seedu.addressbook.password;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Logger;

import seedu.addressbook.PatrolResourceStatus;
import seedu.addressbook.common.Messages;
import seedu.addressbook.parser.Parser;
import seedu.addressbook.readandwrite.ReaderAndWriter;

//@@author iamputradanish

/**
 * Password security for the system. All functionality related to password is stored here.
 */
public class Password {

    public static final String MESSAGE_TRY_AGAIN = "Please try again.";
    public static final String MESSAGE_ENTER_PASSWORD = "Please enter password: ";
    public static final String MESSAGE_ENTER_COMMAND = "Please enter a command: ";
    public static final String MESSAGE_WELCOME = "Welcome %s.";
    public static final String MESSAGE_UNAUTHORIZED = "You are not authorized to ADD, CLEAR, CHECK, DELETE, DISPATCH,"
            + " " + "EDIT, UPDATE PASSWORD nor UPDATE STATUS.";
    public static final String MESSAGE_INCORRECT_PASSWORD = "Password is incorrect. " + MESSAGE_TRY_AGAIN;
    public static final String MESSAGE_ATTEMPTS_LEFT = "You have %1$d attempts left. ";
    public static final String MESSAGE_ATTEMPT_LEFT = "You have %1$d attempt left. ";
    public static final String MESSAGE_SHUTDOWN_WARNING = "System will shut down if password is incorrect. ";
    public static final String MESSAGE_SHUTDOWN = "Password is incorrect. System will shut down. ";
    public static final String MESSAGE_ENTER_PASSWORD_TO_CHANGE = "Please enter current password to change: ";
    public static final String MESSAGE_HQP = "Headquarters Personnel";
    public static final String MESSAGE_PO = "Police Officer ";
    public static final String MESSAGE_ONE = "Oscar November Echo";
    public static final String MESSAGE_TWO = "Tango Whiskey Oscar";
    public static final String MESSAGE_THREE = "Tango Hotel Romeo Echo Echo";
    public static final String MESSAGE_FOUR = "Foxtrot Oscar Uniform Romeo";
    public static final String MESSAGE_FIVE = "Foxtrot India Victor Echo";
    public static final String MESSAGE_ENTER_NEW_PASSWORD = "Please enter new alphanumeric password for ";
    public static final String MESSAGE_ENTER_NEW_PASSWORD_AGAIN = "Please enter new alphanumeric password again: ";
    public static final String MESSAGE_UPDATED_PASSWORD = "You have updated %s password successfully. ";
    public static final String MESSAGE_NOT_SAME = "The password you entered is not the same. ";
    public static final String MESSAGE_AT_LEAST_ONE = "Your new password must contain at least one %s. ";
    public static final String MESSAGE_PASSWORD_EXISTS = "Your new password cannot be the same as an existing password."
            + " ";
    public static final String MESSAGE_PASSWORD_LENGTH = "Your new password is %1$d character(s) long. ";
    public static final String MESSAGE_PASSWORD_MINIMUM_LENGTH = "Your new password must be at least %1$d characters "
            + "long. ";
    public static final String UPDATE_PASSWORD_COMMAND_WORD = "update password";
    public static final String UPDATE_PASSWORD_MESSAGE_USAGE = UPDATE_PASSWORD_COMMAND_WORD
            + ":\n" + "Updates a password\n\t" + "Example: " + UPDATE_PASSWORD_COMMAND_WORD;
    public static final String MESSAGE_VALID = "valid";

    private static final Logger logr = Logger.getLogger(Password.class.getName());
    private static final String MESSAGE_TRY_UNAUTHORIZED = "You are unauthorized to %s.\n"
            + "Please try a different command. ";

    private static int wrongPasswordCounter = 5;

    private static boolean isHqp = false;

    private static boolean isPO1 = false;
    private static boolean isPO2 = false;
    private static boolean isPO3 = false;
    private static boolean isPO4 = false;
    private static boolean isPO5 = false;

    private static boolean isUpdatingPassword = false;

    private static boolean isUpdatePasswordConfirm = false;

    private static boolean isShutDown = false;
    private static String oneTimePassword = null;

    private static ReaderAndWriter readerandwriter = new ReaderAndWriter();

    private static boolean isLoginHqp = false;
    private static boolean isLoginPO1 = false;
    private static boolean isLoginPO2 = false;
    private static boolean isLoginPO3 = false;
    private static boolean isLoginPO4 = false;
    private static boolean isLoginPO5 = false;

    public static void setupLogger() {
        Parser.setupLoggerForAll(logr);
    }

    public static int getWrongPasswordCounter() {
        return wrongPasswordCounter;
    }
    public static void setWrongPasswordCounter(int number) {
        wrongPasswordCounter = (number >= 0) ? number : 0;
    }

    public static void unlockHqp() {
        isHqp = true;
    }

    /**
     * Logs into System as all PO, for testing purposes
     */
    public static void unlockPo() {
        isPO1 = true;
        isPO2 = true;
        isPO3 = true;
        isPO4 = true;
        isPO5 = true;
    }

    public static void lockIsHqp() {
        isHqp = false;
    }

    /**
     * Logs out of System for all PO, for testing purposes
     */
    public static void lockIsPo() {
        isPO1 = false;
        isPO2 = false;
        isPO3 = false;
        isPO4 = false;
        isPO5 = false;
    }
    public static boolean isHqpUser() {
        return isHqp;
    }
    public static boolean isLocked() {
        return !(isHqp || isPo());
    }

    public static boolean isPo() {
        return (isPO1 || isPO2 || isPO3 || isPO4 || isPO5);
    }

    public static boolean isUpdatingPasswordNow() {
        return isUpdatingPassword;
    }

    private static void lockUpdatingPassword() {
        isUpdatingPassword = false;
    }

    private static void lockUpdatePasswordConfirm() {
        isUpdatePasswordConfirm = false;
    }

    /**
     * Logs out user from System and resets boolean flags for updating passwords.
     */
    public static void lockDevice() {
        lockIsHqp();
        lockIsPo();
        lockUpdatePasswordConfirm();
        lockUpdatingPassword();
    }

    public static boolean getIsUpdatingPassword() {
        return isUpdatingPassword;
    }
    public static void unprepareUpdatePassword() {
        isUpdatingPassword = false;
    }
    public static boolean isUpdatePasswordConfirmNow() {
        return isUpdatePasswordConfirm;
    }
    public static void notUpdatingFinal() {
        isUpdatePasswordConfirm = false;
    }

    private static void setUpdatingFinal() {
        isUpdatePasswordConfirm = true;
    }

    /**
     * Resets to start of updating password for testing purposes
     */

    public static void logoutUser() {
        isLoginHqp = false;
        isLoginPO1 = false;
        isLoginPO2 = false;
        isLoginPO3 = false;
        isLoginPO4 = false;
        isLoginPO5 = false;
    }


    private static boolean isNotLogin() {
        return (!isLoginHqp && !isLoginPo());
    }
    private static boolean isLoginPo() {
        return (isLoginPO1 || isLoginPO2 || isLoginPO3 || isLoginPO4 || isLoginPO5);
    }


    public static boolean isShutDown() {
        return isShutDown;
    }

    public static void setOtp(String input) {
        oneTimePassword = input;
    }

    /**
     * The main implementation of logging into the System.
     * Takes the user input and attempts to match with an existing password in the System.
     */
    public static String unlockDevice(String userCommandText, int number) throws IOException {
        logr.info("Unlocking the system.");
        String result = null;

        File originalFile = readerandwriter.fileToUse("passwordStorage.txt");
        BufferedReader br = readerandwriter.openReader(originalFile);


        int hashedEnteredPassword = userCommandText.hashCode();

        String line;
        int numberOfPasswords = 6;

        while (numberOfPasswords > 0) {
            line = br.readLine();
            String storedCurrPassword = line.substring(line.lastIndexOf(" ") + 1);
            String user = line.substring(0, line.lastIndexOf(" "));

            if (correctHqp(user, storedCurrPassword, hashedEnteredPassword)) {
                isHqp = true;
                result = String.format(MESSAGE_WELCOME, MESSAGE_HQP) + "\n"
                        + MESSAGE_ENTER_COMMAND;
                logr.info("Logged in as HQP");
                break;
            } else if (correctPO1(user, storedCurrPassword, hashedEnteredPassword)) {
                isPO1 = true;
                result = String.format(MESSAGE_WELCOME, MESSAGE_PO + MESSAGE_ONE) + "\n"
                        + MESSAGE_UNAUTHORIZED + "\n"
                        + MESSAGE_ENTER_COMMAND;
                logr.info("Logged in as PO1");
                break;
            } else if (correctPO2(user, storedCurrPassword, hashedEnteredPassword)) {
                isPO2 = true;
                result = String.format(MESSAGE_WELCOME, MESSAGE_PO + MESSAGE_TWO) + "\n"
                        + MESSAGE_UNAUTHORIZED + "\n"
                        + MESSAGE_ENTER_COMMAND;
                logr.info("Logged in as PO2");
                break;
            } else if (correctPO3(user, storedCurrPassword, hashedEnteredPassword)) {
                isPO3 = true;
                result = String.format(MESSAGE_WELCOME, MESSAGE_PO + MESSAGE_THREE) + "\n"
                        + MESSAGE_UNAUTHORIZED + "\n"
                        + MESSAGE_ENTER_COMMAND;
                logr.info("Logged in as PO3");
                break;
            } else if (correctPO4(user, storedCurrPassword, hashedEnteredPassword)) {
                isPO4 = true;
                result = String.format(MESSAGE_WELCOME, MESSAGE_PO + MESSAGE_FOUR) + "\n"
                        + MESSAGE_UNAUTHORIZED + "\n"
                        + MESSAGE_ENTER_COMMAND;
                logr.info("Logged in as PO4");
                break;
            } else if (correctPO5(user, storedCurrPassword, hashedEnteredPassword)) {
                isPO5 = true;
                result = String.format(MESSAGE_WELCOME, MESSAGE_PO + MESSAGE_FIVE) + "\n"
                        + MESSAGE_UNAUTHORIZED + "\n"
                        + MESSAGE_ENTER_COMMAND;
                logr.info("Logged in as PO5");
                break;
            } else {
                result = Messages.MESSAGE_ERROR;
                logr.info("Error in logging in.");
            }
            numberOfPasswords--;
        }
        if (isLocked()) {
            result = wrongPasswordShutDown(number);
            logr.info("Shutdown sequence running.");
        } else {
            isShutDown = false;
            logr.info("Shutdown sequence aborted.");
        }
        br.close();
        return result;
    }

    /**
     * When wrong password is entered, this method sequence runs.
     * The number of tries left decreases with each wrong attempt.
     * When wrong password is entered after 5 tries, System will shut down.
     */
    private static String wrongPasswordShutDown(int number) {
        String result;
        if (wrongPasswordCounter > 1) {
            result = MESSAGE_INCORRECT_PASSWORD
                    + "\n" + String.format(MESSAGE_ATTEMPTS_LEFT, number)
                    + "\n" + MESSAGE_ENTER_PASSWORD;
            decreaseWrongPasswordCounter();
        } else if (wrongPasswordCounter == 1) {
            result = MESSAGE_INCORRECT_PASSWORD
                    + "\n" + String.format(MESSAGE_ATTEMPT_LEFT, number)
                    + "\n" + MESSAGE_SHUTDOWN_WARNING;
            decreaseWrongPasswordCounter();
        } else if (wrongPasswordCounter == 0) {
            isShutDown = true;
            result = MESSAGE_SHUTDOWN;
            logr.info("Shutdown imminent.");
        } else {
            result = Messages.MESSAGE_ERROR;
            logr.info("Error in shutdown sequence");
        }
        return result;
    }

    private static void decreaseWrongPasswordCounter() {
        wrongPasswordCounter--;
    }

    public static boolean correctPassword(String storedCurrPassword, int hashedEnteredPassword) {
        return storedCurrPassword.equals(Integer.toString(hashedEnteredPassword));
    }

    /**
     * Returns true when hqp password is entered
     */
    public static boolean correctHqp(String user, String storedCurrPassword, int hashedEnteredPassword) {
        return user.equals(PatrolResourceStatus.HEADQUARTER_PERSONNEL_ID)
                && correctPassword(storedCurrPassword, hashedEnteredPassword);
    }

    /**
     * Returns true when po1 password is entered
     */
    public static boolean correctPO1(String user, String storedCurrPassword, int hashedEnteredPassword) {
        return user.equals(PatrolResourceStatus.POLICE_OFFICER_1_ID)
                && correctPassword(storedCurrPassword, hashedEnteredPassword);
    }

    /**
     * Returns true when po2 password is entered
     */
    public static boolean correctPO2(String user, String storedCurrPassword, int hashedEnteredPassword) {
        return user.equals(PatrolResourceStatus.POLICE_OFFICER_2_ID)
                && correctPassword(storedCurrPassword, hashedEnteredPassword);
    }

    /**
     * Returns true when po3 password is entered
     */
    public static boolean correctPO3(String user, String storedCurrPassword, int hashedEnteredPassword) {
        return user.equals(PatrolResourceStatus.POLICE_OFFICER_3_ID)
                && correctPassword(storedCurrPassword, hashedEnteredPassword);
    }

    /**
     * Returns true when po4 password is entered
     */
    public static boolean correctPO4(String user, String storedCurrPassword, int hashedEnteredPassword) {
        return user.equals(PatrolResourceStatus.POLICE_OFFICER_4_ID)
                && correctPassword(storedCurrPassword, hashedEnteredPassword);
    }

    /**
     * Returns true when po5 password is entered
     */
    public static boolean correctPO5(String user, String storedCurrPassword, int hashedEnteredPassword) {
        return user.equals(PatrolResourceStatus.POLICE_OFFICER_5_ID)
                && correctPassword(storedCurrPassword, hashedEnteredPassword);
    }

    /**
     * Brings updating password sequence to stage 2
     */
    public static String prepareUpdatePassword() {
        isUpdatingPassword = true;
        setWrongPasswordCounter(5);
        return MESSAGE_ENTER_PASSWORD_TO_CHANGE;
    }

    /**
     * TODO: Add javadoc comment
     */
    public static String updatePassword(String userCommandText, int number) throws Exception {
        logr.info("Update sequence stage 1 initiated.");
        String result = null;

        File originalFile = readerandwriter.fileToUse("passwordStorage.txt");
        BufferedReader br = readerandwriter.openReader(originalFile);

        String line;

        if (isNotLogin()) {

            int enteredCurrentPassword = userCommandText.hashCode();

            int numberOfPasswords = 6;

            while (numberOfPasswords > 0) {
                line = br.readLine();
                String storedCurrPassword = line.substring(line.lastIndexOf(" ") + 1);
                String user = line.substring(0, line.lastIndexOf(" "));

                if (correctPassword(storedCurrPassword, enteredCurrentPassword)) {
                    setWrongPasswordCounter(5);

                    if (PatrolResourceStatus.HEADQUARTER_PERSONNEL_ID.equals(user)) {
                        isLoginHqp = true;
                        result = MESSAGE_ENTER_NEW_PASSWORD + MESSAGE_HQP + ":";
                        logr.info("Updating HQP password.");

                    } else if (PatrolResourceStatus.POLICE_OFFICER_1_ID.equals(user)) {
                        isLoginPO1 = true;
                        result = MESSAGE_ENTER_NEW_PASSWORD + MESSAGE_PO + MESSAGE_ONE + ":";
                        logr.info("Updating PO1 password.");

                    } else if (PatrolResourceStatus.POLICE_OFFICER_2_ID.equals(user)) {
                        isLoginPO2 = true;
                        result = MESSAGE_ENTER_NEW_PASSWORD + MESSAGE_PO + MESSAGE_TWO + ":";
                        logr.info("Updating PO2 password.");

                    } else if (PatrolResourceStatus.POLICE_OFFICER_3_ID.equals(user)) {
                        isLoginPO3 = true;
                        result = MESSAGE_ENTER_NEW_PASSWORD + MESSAGE_PO + MESSAGE_THREE + ":";
                        logr.info("Updating PO3 password.");

                    } else if (PatrolResourceStatus.POLICE_OFFICER_4_ID.equals(user)) {
                        isLoginPO4 = true;
                        result = MESSAGE_ENTER_NEW_PASSWORD + MESSAGE_PO + MESSAGE_FOUR + ":";
                        logr.info("Updating PO4 password.");

                    } else if (PatrolResourceStatus.POLICE_OFFICER_5_ID.equals(user)) {
                        isLoginPO5 = true;
                        result = MESSAGE_ENTER_NEW_PASSWORD + MESSAGE_PO + MESSAGE_FIVE + ":";
                        logr.info("Updating PO5 password.");

                    }

                }
                numberOfPasswords--;
            }
            if (isNotLogin()) {
                result = wrongPasswordShutDown(number);
                logr.info("Shutdown sequence running.");
            }
        } else {
            result = passwordValidityChecker(userCommandText);
            if (result.equals(MESSAGE_VALID)) {
                setOtp(userCommandText);
                setUpdatingFinal();
                result = MESSAGE_ENTER_NEW_PASSWORD_AGAIN;
                logr.info("Prompting user to re-enter new password.");
            }
        }
        br.close();
        return result;
    }

    private static boolean isEqualPassword(String secondPassword) {
        return secondPassword.equals(oneTimePassword);
    }

    /**
     * TODO: Add javadoc comment
     */
    public static String updatePasswordFinal(String userCommandText) throws IOException {
        logr.info("Update password sequence stage 2 initiated.");
        String result = null;
        int lineNumber = 0;
        int linesLeft;

        File originalFile = readerandwriter.fileToUse("passwordStorage.txt");
        BufferedReader br = readerandwriter.openReader(originalFile);
        File tempFile = readerandwriter.fileToUse("tempFile.txt");
        PrintWriter pw = readerandwriter.openTempWriter(tempFile);

        String line;

        if (isEqualPassword(userCommandText)) {
            int storedNewPassword = userCommandText.hashCode();

            if (isLoginHqp) {
                isLoginHqp = false;
                result = String.format(MESSAGE_UPDATED_PASSWORD, MESSAGE_HQP);
                logr.info("Updated HQP password.");
            } else if (isLoginPO1) {
                lineNumber = 1;
                isLoginPO1 = false;
                result = String.format(MESSAGE_UPDATED_PASSWORD, MESSAGE_PO + MESSAGE_ONE);
                logr.info("Updated PO1 password.");
            } else if (isLoginPO2) {
                lineNumber = 2;
                isLoginPO2 = false;
                result = String.format(MESSAGE_UPDATED_PASSWORD, MESSAGE_PO + MESSAGE_TWO);
                logr.info("Updated PO2 password.");
            } else if (isLoginPO3) {
                lineNumber = 3;
                isLoginPO3 = false;
                result = String.format(MESSAGE_UPDATED_PASSWORD, MESSAGE_PO + MESSAGE_THREE);
                logr.info("Updated PO3 password.");
            } else if (isLoginPO4) {
                lineNumber = 4;
                isLoginPO4 = false;
                result = String.format(MESSAGE_UPDATED_PASSWORD, MESSAGE_PO + MESSAGE_FOUR);
                logr.info("Updated PO4 password.");
            } else if (isLoginPO5) {
                lineNumber = 5;
                isLoginPO5 = false;
                result = String.format(MESSAGE_UPDATED_PASSWORD, MESSAGE_PO + MESSAGE_FIVE);
                logr.info("Updated PO5 password.");
            }
            unprepareUpdatePassword();
            notUpdatingFinal();
            linesLeft = 5 - lineNumber;
            while (lineNumber > 0) {
                reprintLine(br, pw);
                lineNumber--;
            }
            line = br.readLine();
            line = line.substring(0, line.lastIndexOf(" ") + 1) + Integer.toString(storedNewPassword);
            pw.println(line);
            pw.flush();
            while (linesLeft > 0) {
                reprintLine(br, pw);
                linesLeft--;
            }
            pw.close();
            br.close();

            if (!originalFile.delete()) {
                result = (Messages.MESSAGE_ERROR);
                logr.info("Unable to locate file to delete.");
            }
            if (!tempFile.renameTo(originalFile)) {
                result = (Messages.MESSAGE_ERROR);
                logr.info("Unable to locate file to rename.");
            }
            result = result
                    + "\n" + MESSAGE_ENTER_COMMAND;
        } else {
            notUpdatingFinal();
            result = MESSAGE_NOT_SAME
                    + "\n" + MESSAGE_TRY_AGAIN;
            logr.info("Update password stage 2 does not match stage 1.");
        }
        pw.close();
        br.close();
        return result;
    }

    private static void reprintLine(BufferedReader br, PrintWriter pw) throws IOException {
        String line = br.readLine();
        pw.println(line);
        pw.flush();
    }

    /**
     * TODO: Add javadoc comment
     */
    public static String passwordExistsChecker(String newEnteredPassword) throws IOException {
        logr.info("Checking password validity.");
        String result = MESSAGE_VALID;

        File originalFile = readerandwriter.fileToUse("passwordStorage.txt");
        BufferedReader br = readerandwriter.openReader(originalFile);

        int hashedEnteredPassword = newEnteredPassword.hashCode();

        String line;
        int numberOfPasswords = 6;

        while (numberOfPasswords > 0) {
            line = br.readLine();
            String storedCurrPassword = line.substring(line.lastIndexOf(" ") + 1);
            if (correctPassword(storedCurrPassword, hashedEnteredPassword)) {
                result = MESSAGE_PASSWORD_EXISTS;
                logr.info("New password already exists in system.");
            }
            numberOfPasswords--;
        }
        br.close();
        return result;
    }

    /**
     * TODO: Add javadoc comment
     */
    public static String passwordAlphanumericChecker(String newEnteredPassword) {
        String result;
        if (newEnteredPassword.matches(".*\\d+.*") && newEnteredPassword.matches(".*[a-zA-Z]+.*")) {
            result = MESSAGE_VALID;
        } else if (newEnteredPassword.matches(".*\\d+.*") && !newEnteredPassword.matches(".*[a-zA-Z]+.*")) {
            result = String.format(MESSAGE_AT_LEAST_ONE, "alphabet");
            logr.info("New password missing alphabet.");
        } else if (!newEnteredPassword.matches(".*\\d+.*") && newEnteredPassword.matches(".*[a-zA-Z]+.*")) {
            result = String.format(MESSAGE_AT_LEAST_ONE, "number");
            logr.info("New password missing number.");
        } else {
            result = String.format(MESSAGE_AT_LEAST_ONE, "alphabet and at least one number");
            logr.info("New password missing alphabet and number.");
        }
        return result;
    }

    /**
     * TODO: Add javadoc comment
     */
    public static String passwordLengthChecker(String newEnteredPassword) {
        String result = MESSAGE_VALID;
        int lengthPassword = newEnteredPassword.length();
        int minNumPassword = 5;
        if (lengthPassword < minNumPassword) {
            result = String.format(MESSAGE_PASSWORD_LENGTH, lengthPassword)
                    + "\n" + String.format(MESSAGE_PASSWORD_MINIMUM_LENGTH, minNumPassword);
            logr.info("New password too short.");
        }
        return result;
    }

    /**
     * TODO: Add javadoc comment
     */
    public static String passwordValidityChecker(String newEnteredPassword) throws IOException {
        logr.info("New password checked for validity.");
        String result = MESSAGE_VALID;
        if (!passwordExistsChecker(newEnteredPassword).equals(MESSAGE_VALID)) {
            result = passwordExistsChecker(newEnteredPassword);
        } else if (!passwordAlphanumericChecker(newEnteredPassword).equals(MESSAGE_VALID)
                && !passwordLengthChecker(newEnteredPassword).equals(MESSAGE_VALID)) {
            result = passwordLengthChecker(newEnteredPassword)
                    + "\n" + passwordAlphanumericChecker(newEnteredPassword);
        } else if (!passwordAlphanumericChecker(newEnteredPassword).equals(MESSAGE_VALID)) {
            result = passwordAlphanumericChecker(newEnteredPassword);
        } else if (!passwordLengthChecker(newEnteredPassword).equals(MESSAGE_VALID)) {
            result = passwordLengthChecker(newEnteredPassword);
        }
        return (result.equals(MESSAGE_VALID)) ? MESSAGE_VALID : result + "\n" + MESSAGE_TRY_AGAIN;
    }

    public static String getUnauthorizedPoCommand(String input) {
        logr.info("Checking if PO command is unauthorized.");
        String commandWord;
        if (isRejectPo(input)) {
            commandWord = input;
        } else {
            input += " ";
            commandWord = input.substring(0, input.indexOf(" "));
        }
        return commandWord;
    }

    public static boolean isUnauthorizedAccess(String input) {
        return isPo() && invalidPoCommand(input);
    }

    private static boolean invalidPoCommand(String input) {
        String userCommandWord = getUnauthorizedPoCommand(input);
        return isRejectPo(userCommandWord);
    }
    /**
     * TODO: Add javadoc comment
     */
    public static boolean isRejectPo(String userCommandWord) {
        logr.info("PO command unauthorized.");
        return (userCommandWord.equals("add")
                || userCommandWord.equals("check")
                || userCommandWord.equals("clear")
                || userCommandWord.equals("delete")
                || userCommandWord.equals("dispatch")
                || userCommandWord.equals("edit")
                || userCommandWord.equals("update password")
                || userCommandWord.equals("updatestatus"));
    }

    public static String invalidPoResult(String userCommandText) {
        return String.format(MESSAGE_TRY_UNAUTHORIZED, getUnauthorizedPoCommand(userCommandText));
    }
    public static String getId() {
        logr.info("Obtained user ID");
        if (isHqp) {
            return PatrolResourceStatus.HEADQUARTER_PERSONNEL_ID;
        } else if (isPO1) {
            return PatrolResourceStatus.POLICE_OFFICER_1_ID;
        } else if (isPO2) {
            return PatrolResourceStatus.POLICE_OFFICER_2_ID;
        } else if (isPO3) {
            return PatrolResourceStatus.POLICE_OFFICER_3_ID;
        } else if (isPO4) {
            return PatrolResourceStatus.POLICE_OFFICER_4_ID;
        } else if (isPO5) {
            return PatrolResourceStatus.POLICE_OFFICER_5_ID;
        }
        return "Ghost";
    }

    public static String getFullId(String id) {
        logr.info("Obtained user full ID");
        String result = "Ghost";
        if (PatrolResourceStatus.HEADQUARTER_PERSONNEL_ID.equals(id)) {
            result = MESSAGE_HQP;

        } else if (PatrolResourceStatus.POLICE_OFFICER_1_ID.equals(id)) {
            result = MESSAGE_PO + MESSAGE_ONE;

        } else if (PatrolResourceStatus.POLICE_OFFICER_2_ID.equals(id)) {
            result = MESSAGE_PO + MESSAGE_TWO;

        } else if (PatrolResourceStatus.POLICE_OFFICER_3_ID.equals(id)) {
            result = MESSAGE_PO + MESSAGE_THREE;

        } else if (PatrolResourceStatus.POLICE_OFFICER_4_ID.equals(id)) {
            result = MESSAGE_PO + MESSAGE_FOUR;

        } else if (PatrolResourceStatus.POLICE_OFFICER_5_ID.equals(id)) {
            result = MESSAGE_PO + MESSAGE_FIVE;

        }

        return result;
    }
}

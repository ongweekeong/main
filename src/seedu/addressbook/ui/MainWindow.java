package seedu.addressbook.ui;

import static seedu.addressbook.common.Messages.MESSAGE_USING_STORAGE_FILE;
import static seedu.addressbook.common.Messages.MESSAGE_WELCOME_INITIAL;

import static seedu.addressbook.password.Password.MESSAGE_ENTER_PASSWORD;
import static seedu.addressbook.password.Password.UPDATE_PASSWORD_COMMAND_WORD;

import static seedu.addressbook.password.Password.invalidPoResult;
import static seedu.addressbook.password.Password.isHqpUser;
import static seedu.addressbook.password.Password.isShutDown;
import static seedu.addressbook.password.Password.isUnauthorizedAccess;
import static seedu.addressbook.password.Password.isUpdatePasswordConfirmNow;
import static seedu.addressbook.password.Password.isUpdatingPasswordNow;
import static seedu.addressbook.password.Password.lockDevice;
import static seedu.addressbook.password.Password.prepareUpdatePassword;
import static seedu.addressbook.password.Password.unlockDevice;
import static seedu.addressbook.password.Password.updatePassword;
import static seedu.addressbook.password.Password.updatePasswordFinal;

import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import seedu.addressbook.autocorrect.AutoCorrect;
import seedu.addressbook.autocorrect.CheckDistance;
import seedu.addressbook.commands.CommandResult;
import seedu.addressbook.commands.LogoutCommand;
import seedu.addressbook.commands.ShutdownCommand;
import seedu.addressbook.data.person.ReadOnlyPerson;
import seedu.addressbook.logic.Logic;
import seedu.addressbook.password.Password;
import seedu.addressbook.timeanddate.TimeAndDate;


/**
 * Main Window of the GUI.
 */
public class MainWindow {

    private TimeAndDate tad = new TimeAndDate();
    private Logic logic;
    private Stoppable mainApp;

    @FXML
    private TextArea outputConsole;

    @FXML
    private TextField commandInput;

    public MainWindow() {
    }

    /**
     *
     * @param event
     */
    @FXML
    void onCommand(ActionEvent event) {
        try {
            String userCommandText = commandInput.getText();
            decipherUserCommandText(userCommandText);
        } catch (Exception e) {
            e.printStackTrace();
            display(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    void setLogic(Logic logic) {
        this.logic = logic;
    }

    void setMainApp(Stoppable mainApp) {
        this.mainApp = mainApp;
    }

    //@@author iamputradanish

    /**
     * Takes the user input, parses it, then displays result of input accordingly.
     * @param userCommandText
     * @throws Exception
     */
    private void decipherUserCommandText(String userCommandText) throws Exception {
        Password.setupLogger();
        if (toCloseApp(userCommandText)) {
            lockDevice();
            mainApp.stop();
        } else if (isLogoutCommand(userCommandText)) {
            CommandResult result = logic.execute(userCommandText);
            clearScreen();
            displayResult(result);
        } else if (Password.isLocked()) {
            String unlockDeviceResult = unlockDevice(userCommandText, Password.getWrongPasswordCounter());
            clearScreen();
            display(unlockDeviceResult);
        } else if (canUpdatePassword(userCommandText)) {
            String prepareUpdatePasswordResult = prepareUpdatePassword();
            clearScreen();
            display(prepareUpdatePasswordResult);
        } else if (isUpdatingPasswordNow()) {
            String updatePasswordResult;
            if (isUpdatePasswordConfirmNow()) {
                updatePasswordResult = updatePasswordFinal(userCommandText);
            } else {
                updatePasswordResult = updatePassword(userCommandText, Password.getWrongPasswordCounter());
            }
            clearScreen();
            display(updatePasswordResult);
        } else if (isUnauthorizedAccess(userCommandText)) {
            clearScreen();
            String unauthorizedCommandResult = invalidPoResult(userCommandText);
            display(unauthorizedCommandResult);
        } else {
            //@@author ShreyasKp
            userCommandText = userCommandText.trim();
            CheckDistance checker = new CheckDistance();

            //Extract the command from the user input
            String commandWordInput = AutoCorrect.getCommand(userCommandText);

            //Checks if the command word is invalid.
            // If invalid, it runs the autocorrection, if valid, it runs the Logic class
            if ((checker.predictionChecker(commandWordInput))) {
                clearScreen();
                AutoCorrect correction = new AutoCorrect();
                String displayCommand = correction.getResultOfInvalidCommand(commandWordInput);
                display(displayCommand);
            } else {
                clearScreen();
                CommandResult result = logic.execute(userCommandText);
                displayResult(result);
                clearCommandInput();
            }
        }
    }

    /** Returns true when user is HQP and given input is update password command*/
    //@@author iamputradanish
    private boolean canUpdatePassword(String userCommandText) {
        return isHqpUser() && isUpdatePasswordCommand(userCommandText);
    }

    //@@author
    /** Returns true if the result given is the result of an exit command */
    private boolean isExitCommand(String userCommandText) {
        return userCommandText.equals(ShutdownCommand.COMMAND_WORD);
    }

    /** Returns true if the input given is the update password command */
    //@@author iamputradanish
    private boolean isUpdatePasswordCommand(String userCommandText) {
        return userCommandText.equals(UPDATE_PASSWORD_COMMAND_WORD);
    }

    /** Returns true when shutting down system*/
    private boolean toCloseApp(String userCommandText) {
        return isExitCommand(userCommandText) || isShutDown();
    }

    /** Returns true if the input given is a logout command */
    private boolean isLogoutCommand(String userCommandText) {
        return userCommandText.equals(LogoutCommand.COMMAND_WORD);
    }

    //@@author
    /** Clears the command input box */
    private void clearCommandInput() {
        commandInput.setText("");
    }

    /** Clears the output display area */
    private void clearOutputConsole() {
        outputConsole.clear();
    }

    /** Clears both command input box and the output display area*/
    //@@author iamputradanish
    private void clearScreen() {
        clearCommandInput();
        clearOutputConsole();
    }

    //@@author
    /** Displays the result of a command execution to the user. */
    private void displayResult(CommandResult result) {
        clearOutputConsole();
        final Optional<List<? extends ReadOnlyPerson>> resultPersons = result.getRelevantPersons();
        resultPersons.ifPresent(this::display);
        display(result.feedbackToUser);
    }

    void displayWelcomeMessage(String version, String storageFilePath) {
        String storageFileInfo = String.format(MESSAGE_USING_STORAGE_FILE, storageFilePath);
        display(MESSAGE_WELCOME_INITIAL, version, storageFileInfo, tad.outputDatMainHrs() + "\n" ,
                MESSAGE_ENTER_PASSWORD);
    }

    /**
     * Displays the list of persons in the output display area, formatted as an indexed list.
     * Private contact details are hidden.
     */
    private void display(List<? extends ReadOnlyPerson> persons) {
        display(new UiFormatter().format(persons));
    }

    /**
     * Displays the given messages on the output display area, after formatting appropriately.
     */
    private void display(String... messages) {
        outputConsole.setText(outputConsole.getText() + new UiFormatter().format(messages));
    }

}

package seedu.addressbook.ui;

import static seedu.addressbook.common.Messages.MESSAGE_USING_STORAGE_FILE;
import static seedu.addressbook.common.Messages.MESSAGE_WELCOME;

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

    private Password password = new Password();
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
     * TODO: Add Javadoc comment
     * @param userCommandText
     * @throws Exception
     */
    private void decipherUserCommandText(String userCommandText) throws Exception {
        Password.setupLogger();
        if (toCloseApp(userCommandText)) {
            password.lockDevice();
            mainApp.stop();
        } else if (isLockCommand(userCommandText)) {
            CommandResult result = logic.execute(userCommandText);
            clearScreen();
            displayResult(result);
        } else if (Password.isLocked()) {
            String unlockDeviceResult = Password.unlockDevice(userCommandText, Password.getWrongPasswordCounter());
            clearScreen();
            display(unlockDeviceResult);
        } else if (canUpdatePassword(userCommandText)) {
            String prepareUpdatePasswordResult = Password.prepareUpdatePassword();
            clearScreen();
            display(prepareUpdatePasswordResult);
        } else if (password.isUpdatingPasswordNow()) {
            String updatePasswordResult;
            if (Password.isUpdatePasswordConfirmNow()) {
                updatePasswordResult = password.updatePasswordFinal(userCommandText);
            } else {
                updatePasswordResult = password.updatePassword(userCommandText, Password.getWrongPasswordCounter());
            }
            clearScreen();
            display(updatePasswordResult);
        } else if (password.isUnauthorizedAccess(userCommandText)) {
            clearScreen();
            String unauthorizedCommandResult = password.invalidPoResult(userCommandText);
            display(unauthorizedCommandResult);
        } else {
            //@@author ShreyasKp
            userCommandText = userCommandText.trim();
            CheckDistance checker = new CheckDistance();

            //Extract the command from the user input
            String commandWordInput = AutoCorrect.getCommand(userCommandText);

            //Checks if the command word is invalid. If invalid, it runs the autocorrection, if valid, it runs the Logic class
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

    //@@author iamputradanish
    private boolean canUpdatePassword(String userCommandText) {
        return Password.isHqpUser() && isUpdatePasswordCommand(userCommandText);
    }

    //@@author
    /** Returns true if the result given is the result of an exit command */
    private boolean isExitCommand(String userCommandText) {
        return userCommandText.equals(ShutdownCommand.COMMAND_WORD);
    }

    //@@author iamputradanish
    private boolean isUpdatePasswordCommand(String userCommandText) {
        return userCommandText.equals(Password.UPDATE_PASSWORD_COMMAND_WORD);
    }

    private boolean toCloseApp(String userCommandText) {
        return isExitCommand(userCommandText) || password.isShutDown();
    }

    private boolean isLockCommand(String userCommandText) {
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
        display(MESSAGE_WELCOME, version, storageFileInfo, tad.outputDatMainHrs() + "\n" ,
                Password.MESSAGE_ENTER_PASSWORD);
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

    //TODO: If not used, delete
    public void displayTimestamps(List<String> history) {
        display(new UiFormatter().formatForStrings(history));
    }

}

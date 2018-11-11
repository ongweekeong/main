//@@author ShreyasKp
package seedu.addressbook.autocorrect;

import static seedu.addressbook.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;

import seedu.addressbook.commands.AddCommand;
import seedu.addressbook.commands.CheckCommand;
import seedu.addressbook.commands.CheckPoStatusCommand;
import seedu.addressbook.commands.ClearCommand;
import seedu.addressbook.commands.ClearInboxCommand;
import seedu.addressbook.commands.DateTimeCommand;
import seedu.addressbook.commands.DeleteCommand;
import seedu.addressbook.commands.Dictionary;
import seedu.addressbook.commands.DispatchCommand;
import seedu.addressbook.commands.EditCommand;
import seedu.addressbook.commands.FindCommand;
import seedu.addressbook.commands.HelpCommand;
import seedu.addressbook.commands.InboxCommand;
import seedu.addressbook.commands.IncorrectCommand;
import seedu.addressbook.commands.ListCommand;
import seedu.addressbook.commands.LogoutCommand;
import seedu.addressbook.commands.ReadCommand;
import seedu.addressbook.commands.RequestHelpCommand;
import seedu.addressbook.commands.ShowUnreadCommand;
import seedu.addressbook.commands.ShutdownCommand;
import seedu.addressbook.commands.UpdateStatusCommand;
import seedu.addressbook.commands.ViewAllCommand;
import seedu.addressbook.password.Password;

/**
 * Checks if the invalid command has a prediction and returns the valid format for using the command
 */
public class AutoCorrect {

    private CheckDistance checker = new CheckDistance();

    private Dictionary dictionary = new Dictionary();

    /**
     * Extracts the command from the user input
     * @param userInput The input
     * @return The command
     */
    public static String getCommand(String userInput) {
        String[] arr = userInput.split(" ", 2);
        return arr[0];
    }

    public static String getInvalidCommandMessage(boolean isHqpFlag) {
        if (isHqpFlag) {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    HelpCommand.MESSAGE_ALL_USAGES)).feedbackToUser;
        } else {
            return new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                    HelpCommand.MESSAGE_PO_USAGES)).feedbackToUser;
        }
    }

    public String getResultOfInvalidCommand(String userInput) {
        boolean isHqpFlag = Password.isHqpUser();
        String command = checkCommand(userInput, isHqpFlag);
        String output = checker.checkDistance(userInput);
        if (!(output.equals("none")) && isHqpFlag) {
            return (String.format(dictionary.getCommandErrorMessage(), output)) + "\n" + command;
        } else if (!(output.equals("none")) && !isHqpFlag) {
            return command;
        } else {
            return getInvalidCommandMessage(isHqpFlag);
        }
    }

    /**
     * Checks if the invalid command has a prediction and returns valid format of using command if found
     * @param commandInput User Input which is invalid
     * @param isHqp Flag if user os a HQP or not
     * @return Valid format of using command if a prediction is found
     */
    public String checkCommand(String commandInput, boolean isHqp) {
        String output = checker.checkDistance(commandInput);
        String displayCommand;
        if (!(output.equals("none"))) {
            if (AddCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        AddCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (CheckCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        CheckCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (CheckPoStatusCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        CheckPoStatusCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ClearCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ClearCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ClearInboxCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ClearInboxCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (DateTimeCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        DateTimeCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (DeleteCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        DeleteCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (DispatchCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        DispatchCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (EditCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        EditCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (FindCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        FindCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (InboxCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        InboxCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ListCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ListCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (LogoutCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        LogoutCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ReadCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ReadCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (RequestHelpCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        RequestHelpCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ShowUnreadCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ShowUnreadCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ShutdownCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ShutdownCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (UpdateStatusCommand.COMMAND_WORD.equals(output) && isHqp) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        UpdateStatusCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (ViewAllCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        ViewAllCommand.MESSAGE_USAGE)).feedbackToUser;

            } else if (HelpCommand.COMMAND_WORD.equals(output)) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        HelpCommand.MESSAGE_USAGE)).feedbackToUser;

            } else {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        HelpCommand.MESSAGE_PO_USAGES)).feedbackToUser;
            }
            if (isHqp) {
                int i = displayCommand.indexOf("!");
                displayCommand = displayCommand.substring(i + 1);
            } else {
                switch (output) {
                case AddCommand.COMMAND_WORD:
                case CheckCommand.COMMAND_WORD:
                case ClearCommand.COMMAND_WORD:
                case DeleteCommand.COMMAND_WORD:
                case DispatchCommand.COMMAND_WORD:
                case EditCommand.COMMAND_WORD:
                case UpdateStatusCommand.COMMAND_WORD:
                    break;
                default:
                    int i = displayCommand.indexOf("!");
                    displayCommand = displayCommand.substring(i + 1);
                    displayCommand = (String.format(dictionary.getCommandErrorMessage(), output)) + "\n"
                            + displayCommand;
                }
            }
        } else {
            boolean isHqpFlag = Password.isHqpUser();
            if (isHqpFlag) {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        HelpCommand.MESSAGE_ALL_USAGES)).feedbackToUser;
            } else {
                displayCommand = new IncorrectCommand(String.format(MESSAGE_INVALID_COMMAND_FORMAT,
                        HelpCommand.MESSAGE_PO_USAGES)).feedbackToUser;
            }
        }
        return displayCommand;
    }
}

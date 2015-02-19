package command;

import pbsc.*;

public abstract class Command {

    /**
     * Generate the pidgen code for this command.
     * @return Returns the pidgen code as a String.
     */
    public abstract String generateCode();

}

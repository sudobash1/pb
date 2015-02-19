package command;

import pbsc.*;

public abstract class Command {

    private PbscCompiler m_compiler = null;

    public Command(PbscCompiler compiler) {
        m_compiler = compiler;
    }

    /**
     * Generate the pidgen code for this command.
     * @return Returns the pidgen code as a String.
     */
    public abstract String generateCode();

}

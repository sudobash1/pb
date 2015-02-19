package command;

import pbsc.*;

public abstract class Command {

    protected PbscCompiler m_compiler = null;

    protected final String idReStr = "[a-zA-Z_][a-zA-Z0-9_]*";
    protected int m_line;

    public Command(PbscCompiler compiler, int line) {
        m_compiler = compiler;
        m_line = line;
    }

    /**
     * Generate the pidgen code for this command.
     * @return Returns the pidgen code as a String.
     */
    public abstract String generateCode();

}

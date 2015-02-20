package command;

import pbsc.*;

public abstract class Command {

    protected PbscCompiler m_compiler = null;

    protected final String idReStr = "[a-zA-Z_][a-zA-Z0-9_]*";
    protected final String constLitReStr = "(#"+idReStr+"|[1-9][0-9]*)";
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

    /**
     * Returns how many pidgen instructions this command will take.
     * @return The number of pidgen instructions needed for this command.
     */
    public abstract int pidgenInstructionsNeeded();

}

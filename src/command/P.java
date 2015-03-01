package command;

import pbsc.*;
import expression.*;

/**
 * Insert a literal pidgen command.
 */
public class P extends Command {

    private final String m_command;

    /**
     * Insert a literal pidgen command.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param command The literal pidgen command.
     */
    public P(PbscCompiler compiler, int line, String command) {
        super(compiler, line);
        m_command = command;
    }

    @Override
    public String generateCode() {
        return super.generateCode() +
               m_command +
               m_compiler.lineEnding();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

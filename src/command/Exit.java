package command;

import pbsc.*;
import expression.*;

/**
 * Syscall for an EXIT
 */
public class Exit extends Command {

    /**
     * Create an Exit instance for an EXIT.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Exit(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    @Override
    public String generateCode() {
        return
            super.generateCode() +
            "SET R" + tmpRegister0 + " " + m_compiler.SYSCALL_EXIT +
            endl() +
            "PUSH R" + tmpRegister0 + endl() +
            "TRAP" + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}


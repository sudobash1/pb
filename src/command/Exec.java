package command;

import pbsc.*;
import expression.*;

/**
 * Syscall for an EXEC
 */
public class Exec extends Command {

    /**
     * Create an Exec instance for an EXEC.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Exec(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    @Override
    public String generateCode() {
        return
            super.generateCode() +
            "SET R" + tmpRegister0 + " " + m_compiler.SYSCALL_EXEC +
            endl() +
            "PUSH R" + tmpRegister0 + endl() +
            "TRAP" + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

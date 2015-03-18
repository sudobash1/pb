package command;

import pbsc.*;
import expression.*;

/**
 * Syscall for a dump and termination.
 */
public class Dump extends Command {

    /**
     * Create a Dump instance for a dump and termination syscall.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Dump(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    @Override
    public String generateCode() {
        return
            super.generateCode() +
            "SET R" + tmpRegister0 + " " + m_compiler.SYSCALL_COREDUMP +
            endl() +
            "PUSH R" + tmpRegister0 + endl() +
            "TRAP" + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

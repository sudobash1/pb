package command;

import pbsc.*;
import expression.*;

/**
 * Syscall for a yeild.
 */
public class Yield extends Command {

    /**
     * Create a Yeild instance for a yeild syscall.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Yield(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    @Override
    public String generateCode() {
        return
            super.generateCode() +
            "SET R" + tmpRegister0 + " " + m_compiler.SYSCALL_YIELD +
            endl() +
            "PUSH R" + tmpRegister0 + endl() +
            "TRAP" + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

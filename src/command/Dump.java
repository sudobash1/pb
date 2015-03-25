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
    public void generateCode() {
        super.generateCode();
        write("SET", tmpRegister0, m_compiler.SYSCALL_COREDUMP);
        write("PUSH", tmpRegister0);
        write("TRAP");
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

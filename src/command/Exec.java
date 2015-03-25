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
    public void generateCode() {
        super.generateCode();
        write("SET", tmpRegister0, m_compiler.SYSCALL_EXEC);
        write("PUSH", tmpRegister0);
        write("TRAP");
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

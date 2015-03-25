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
    public void generateCode() {
        super.generateCode();
        write("SET", tmpRegister0, m_compiler.SYSCALL_YIELD);
        write("PUSH",tmpRegister0);
        write("TRAP");
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

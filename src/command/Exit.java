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
    public void generateCode() {
        super.generateCode();
        write("SET", tmpRegister0, m_compiler.SYSCALL_EXIT);
        write("PUSH", tmpRegister0);
        write("TRAP");
    }

    @Override
    public int stackReq() {
        return 0;
    }
}


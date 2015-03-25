package command;

import pbsc.*;
import expression.*;

/**
 * Ends a SUB block
 */
public class Bus extends Command {

    private final Sub subLink;

    /**
     * Create a Bus instance to end the SUB block
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Bus(PbscCompiler compiler, int line) {
        super(compiler, line);

        subLink = currentSub;

        if (subLink == null) {
            compiler.error(line, "BUS with no SUB");
            return;
        }

        currentSub = null;
        compiler.popScope();
    }

    @Override
    public void generateCode() {
        super.generateCode();
        write("POP", tmpRegister0);
        write("SET", tmpRegister1, ""+m_compiler.INSTSIZE);
        write("ADD", PbscCompiler.pcRegister, tmpRegister0, tmpRegister1);
        write(":" + subLink.endLabel);
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

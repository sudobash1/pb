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
    public String generateCode() {
        return
            super.generateCode() +
            "POP R" + tmpRegister0 + endl() +
            "SET R" + tmpRegister1 + " " + m_compiler.INSTSIZE + endl() +
            "ADD R" + PbscCompiler.pcRegister  + " R" + tmpRegister0 + " R" +
            tmpRegister1 + endl() +
            ":" + subLink.endLabel + endl();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

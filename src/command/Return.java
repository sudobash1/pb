package command;

import pbsc.*;
import expression.*;

/**
 * Prematurely exit a SUB block
 */
public class Return extends Command {

    private final Sub subLink;

    /**
     * Create a Return instance to exit the SUB block
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Return(PbscCompiler compiler, int line) {
        super(compiler, line);

        subLink = currentSub;

        if (subLink == null) {
            compiler.error(line, "RETURN with no SUB");
            return;
        }
    }

    @Override
    public String generateCode() {
        return
            super.generateCode() +
            "POP R" + tmpRegister0 + endl() +
            "SET R" + tmpRegister1 + " " + m_compiler.INSTSIZE + endl() +
            "ADD R" + pcRegister + " R" + tmpRegister0 + " R" + tmpRegister1 +
            endl();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

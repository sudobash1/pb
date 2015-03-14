package command;

import pbsc.*;
import expression.*;

/**
 * Ends an IF block
 */
public class Fi extends Command {

    private final If ifLink;

    /**
     * End the IF block
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Fi(PbscCompiler compiler, int line) {
        super(compiler, line);

        ifLink = If.currentIf(true);

        if (ifLink == null) {
            compiler.error(line, "FI with no IF");
            return;
        }

        compiler.popScope();
    }

    @Override
    public String generateCode() {
        return super.generateCode() +
               ":" + ifLink.endLabel + endl();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

package command;

import pbsc.*;
import expression.*;

/**
 * Enter the else section of an if block.
 */
public class Else extends Command {

    private final If ifLink;

    protected final String blockID;

    /**
     * Enter the else section of an if block.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Else(PbscCompiler compiler, int line) {
        super(compiler, line);

        ifLink = If.currentIf(false);

        if (ifLink == null) {
            compiler.error(line, "ELSE with no IF");
            blockID = "";
            return;
        }

        blockID = ifLink.blockID + "ELSE";

        if (ifLink.m_foundElse) {
            if (! ifLink.m_foundMultElse) {
                compiler.error(
                    ifLink.m_line, "IF with multiple ELSE statements"
                );
            }
            ifLink.m_foundMultElse = true;
            return;
        }

        ifLink.m_foundElse = true;

        compiler.popScope();
        compiler.pushScope(blockID);
    }

    @Override
    public String generateCode() {
        return super.generateCode() +
               "BRANCH " + ifLink.endLabel + endl() +
               ":" + ifLink.elseLabel + endl();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

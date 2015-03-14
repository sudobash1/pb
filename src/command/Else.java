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
            compiler.error(line, "`else' with no `if'");
            blockID = "";
            return;
        }

        ifLink.m_foundElse = true;

        blockID = ifLink.blockID + "ELSE";
        compiler.popScope();
        compiler.pushScope(blockID);
    }

    @Override
    public String generateCode() {
        return super.generateCode() +
               "BRANCH " + ifLink.endLabel + m_compiler.lineEnding() +
               ":" + ifLink.elseLabel + m_compiler.lineEnding();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

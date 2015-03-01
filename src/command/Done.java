package command;

import pbsc.*;
import expression.*;

/**
 * Ends a WHILE block
 */
public class Done extends Command {

    private final While whileLink;

    /**
     * End the WHILE block
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Done(PbscCompiler compiler, int line) {
        super(compiler, line);

        whileLink = While.currentWhile();

        if (whileLink  == null) {
            compiler.error(line, "`done' with no `while'");
        }

        compiler.popScope();
    }

    @Override
    public String generateCode() {
        return super.generateCode() +
               ":" + whileLink.doneLabel + m_compiler.lineEnding();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

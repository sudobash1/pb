package command;

import pbsc.*;
import expression.*;

/**
 * Ends a WHILE or a FOR block
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

        if (whileLink == null) {
            compiler.error(line, "`done' with no `while' or `for'");
            return;
        }

        compiler.popScope();
    }

    @Override
    public String generateCode() {
        String ret = super.generateCode();
        if (whileLink.postCommand() != null) {
            ret += whileLink.postCommand().generateCode();
        }
        ret += "BRANCH " + whileLink.testLabel;
        ret += ":" + whileLink.doneLabel + m_compiler.lineEnding();
        return ret;
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

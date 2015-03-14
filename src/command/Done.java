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

        whileLink = While.currentWhile(true);

        if (whileLink == null) {
            compiler.error(line, "DONE with no WHILE or FOR");
            return;
        }

        compiler.popScope();
    }

    @Override
    public String generateCode() {
        String ret = super.generateCode();
        if (whileLink.postCommand() != null) {
            if (m_compiler.debugging()) {
                ret += "#Incrementing loop" + endl();
            }
            ret += whileLink.postCommand().generateCode();
        }
        ret += "BRANCH " + whileLink.testLabel;
        if (m_compiler.debugging()) {
            ret += "   #Go back to start of loop";
        }
        ret += endl();
        ret += ":" + whileLink.doneLabel + endl();
        return ret;
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

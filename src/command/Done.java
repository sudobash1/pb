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
    public void generateCode() {

        super.generateCode();

        if (whileLink.postCommand() != null) {
            if (m_compiler.debugging()) {
                write("#Incrementing loop");
            }
            whileLink.postCommand().generateCode();
        }
        if (m_compiler.debugging()) {
            write(
                "BRANCH ", whileLink.testLabel, "  #Go back to start of loop"
            );
        }
        write(":" + whileLink.doneLabel);
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

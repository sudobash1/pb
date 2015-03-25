package command;

import pbsc.*;
import expression.*;

/**
 * Leave a WHILE or a FOR block
 */
public class Break extends Command {

    private final While whileLink;

    /**
     * Leave the WHILE or a FOR block
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Break(PbscCompiler compiler, int line) {
        super(compiler, line);

        whileLink = While.currentWhile(false);

        if (whileLink == null) {
            compiler.error(line, "`Break' with no `while' or `for'");
        }
    }

    @Override
    public void generateCode() {
        super.generateCode();
        write("BRANCH", whileLink.doneLabel);
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

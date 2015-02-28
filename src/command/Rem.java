package command;

import pbsc.*;

/**
 * A command to generate a comment.
 * This command does nothing. All input is ignored.
 */
public class Rem extends Command {

    private String m_comment;

    /**
     * Create a ConstLit expression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public Rem(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

package command;

import pbsc.*;

public class Rem extends Command {

    private String m_comment;

    public Rem(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);
        m_comment = arguments;
    }

    @Override
    public String generateCode() {
        //We will insert the commint into the pidgen file
        return "#" + m_comment + "\n";
    }

    @Override
    public int pidgenInstructionsNeeded() {
        return 0;
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

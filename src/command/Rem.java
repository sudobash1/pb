package command;

import pbsc.*;

public class Rem extends Command {

    private String m_comment;

    public Rem(PbscCompiler compiler, String arguments) {
        super(compiler);
        m_comment = arguments;
    }

    @Override
    public String generateCode() {
        //We will insert the commint into the pidgen file
        return "#" + m_comment + "\n";
    }

}

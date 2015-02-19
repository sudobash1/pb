package command;

import java.util.regex.*;
import pbsc.*;

public class Define extends Command {

    private String m_comment;

    public Define(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        String reStr = "(#" + idReStr + ")\\s*=\\s*([1-9][0-9]*)";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(line ,"Malformed arguments to define.");
            return;
        }

        String name = m.group(1);
        String intStr = m.group(2);
        Integer i = null;

        //Try to parse the argument into an integer.
        try {
            i = Integer.valueOf(intStr);
        } catch (NumberFormatException e) {
            compiler.error(line, "Integer literal out of range, `" + intStr + "'.");
            return;
        }

        compiler.registerNewConstant(name, i, line);
    }

    @Override
    public String generateCode() {
        //No code needs to be executed in actual Pidgin.
        //The constant is replaced at compile time.
        return "";
    }

}

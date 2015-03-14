package command;

import java.util.regex.*;
import pbsc.*;

/**
 * A command to generate a constant.
 * This command generates no pidgen code, but it registers a constant with the
 * PbscCompiler instance.
 * @see PbscCompiler.registerNewConstant
 */
public class Define extends Command {

    /**
     * Register the constant.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments given the DEFINE command.
     */
    public Define(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        if (! arguments.startsWith("#")) {
            compiler.error(line, "define constants must start with `#'.");
            return;
        }

        String reStr = "^(#" + idReStr + ")\\s*=\\s*"+constLitReStr+"$";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(line ,"Malformed arguments to define.");
            return;
        }

        String name = m.group(1);
        String intStr = m.group(2);
        Integer i = m_compiler.constLit2Integer(intStr, line);
        if (i == null) {
            return;
        }

        compiler.registerNewConstant(name, i, line);
    }

    @Override
    public String generateCode() {
        //No code needs to be executed in actual Pidgin.
        //The constant is replaced at compile time.
        return super.generateCode();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

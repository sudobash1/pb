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

        String reStr = "^(#" + idReStr + ")\\s*=\\s*([1-9][0-9]*)$";
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
            compiler.error(
                line,
                "Integer literal out of range, `" + intStr + "'."
            );
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

    @Override
    public int stackReq() {
        return 0;
    }
}

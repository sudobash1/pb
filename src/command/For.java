package command;

import java.util.regex.*;
import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Starts a for block
 */
public class For extends While {

    private final String m_forReStr =
        "^(" + idReStr + ")\\s+(from\\s*" + expressionReStr + ")?\\s*to\\s*" +
        expressionReStr + "\\s*(step\\s*" + expressionReStr + ")?$";

    /**
     * Create a new FOR block.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the FOR command.
     */
    public For(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, null);

        Matcher m = Pattern.compile(m_forReStr).matcher(arguments);
        if (! m.find()) {
            compiler.error(line, "Invalid arguments to FOR.");
        }

        String var = m.group(1);
        String from = m.group(3);
        String to = m.group(9);
        String step = m.group(16);

        //Create the var if it does not exist.
        if (compiler.getVarableDefinition(var, line, false) == null) {
            compiler.registerNewVariable(
                var, new IntDefinition(compiler, line, var), line
            );
        }

        //Create the from command.
        if (from == null) {
            m_preCommand = new Set(compiler, line, var + " = 0");
        } else {
            m_preCommand = new Set(compiler, line, var + " = " + from);
        }

        if (step == null) {
            m_postCommand = new Set(
                compiler, line, var + " = (+ 1 " +var + ")"
            );
        } else {
            m_postCommand = new Set(
                compiler, line, var + " = (+ " + step + " " + var + ")"
            );
        }

        m_exp = Expression.create(
            compiler, line, whileRegister, "(!= " + var + " " + to + ")"
        );
    }
}

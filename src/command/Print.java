package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * A command to print a number to the console.
 * Assumes console is dev 1 and writes to addr 0.
 */
public class Print extends Writer {

    /**regex to extract and check arguments*/
    private final String m_argumentsReStr = 
        "^" + expressionReStr +
        "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**
     * Create a new Print instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the print command.
     */
    public Print(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);

        Matcher m = Pattern.compile(m_argumentsReStr).matcher(arguments);
        if (! m.find()) {
            compiler.error(line, "Malformed arguments to PRINT.");
            return;
        }

        m_output = Expression.create(compiler, line, trapperRegister, m.group(1));
        m_deviceNum = Expression.create(compiler, line, trapperRegister, "1");
        m_deviceAddr = Expression.create(compiler, line, trapperRegister, "0");
    }
}

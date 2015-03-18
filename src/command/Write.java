package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * A command to write a number to a device.
 */
public class Write extends Writer {

    /**regex to extract and check arguments*/
    private final String m_argumentsReStr = 
        "^" + expressionReStr + "\\sto\\s" + expressionReStr +
        "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**
     * Create a new Write instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the WRITE command.
     */
    public Write(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);

        Matcher m = Pattern.compile(m_argumentsReStr).matcher(arguments);
        if (! m.find()) {
            compiler.error(
                line,
                "Malformed arguments to WRITE.\n" +
                "Usage: WRITE <int> TO <int> " +
                "{ON <errorNo> GOTO <label>} [DEFAULT GOTO <label>]"
            );
            return;
        }

        m_output = Expression.create(
            compiler, line, trapperRegister, m.group(1),
            "The output to write"
        );
        m_deviceNum = Expression.create(
            compiler, line, trapperRegister, m.group(7),
            "The device number"
        );
        //TODO: make this customizable.
        m_deviceAddr = Expression.create(
            compiler, line, trapperRegister, "0",
            "The address to write to"
        );

        m_autoOpen = false;
    }
}

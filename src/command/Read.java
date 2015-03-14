package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * A command to read a number from a device.
 */
public class Read extends Reader {

    /**regex to extract and check arguments*/
    private final String m_argumentsReStr = 
        "^(" + idReStr + ")\\sfrom\\s" + expressionReStr +
        "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**
     * Create a new Read instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the PRINT Command.
     */
    public Read(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);

        Matcher m = Pattern.compile(m_argumentsReStr).matcher(arguments);
        if (! m.find()) {
            compiler.error(
                line,
                "Malformed arguments to READ.\n" +
                "Usage: READ <int var> FROM <int> " +
                "{ON <errorNo> GOTO <label>} [DEFAULT GOTO <label>]"
            );
            return;
        }

        m_pointerExp = new IntVariablePointer(
            compiler, line, LRegister, m.group(1)
        );
        m_deviceNum = Expression.create(
            compiler, line, trapperRegister, m.group(2),
            "The device number"
        );
        //TODO: make this customizable.
        m_deviceAddr = Expression.create(
            compiler, line, trapperRegister, "0",
            "The address to read from"
        );

        m_autoOpen = false;
    }
}

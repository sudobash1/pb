package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * A command to read a number from the keyboard.
 * Assumes keyboard is dev 0 and reads from addr 0.
 */
public class Input extends Reader {

    /**regex to extract and check arguments*/
    private final String m_argumentsReStr = 
        "^(" + idReStr + ")" + "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**
     * Create a new Input instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the INPUT command.
     */
    public Input(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);

        Matcher m = Pattern.compile(m_argumentsReStr).matcher(arguments);
        if (! m.find()) {
            compiler.error(
                line,
                "Malformed arguments to INPUT.\n" +
                "Usage: INPUT <int var> {ON <errorNo> GOTO <label>} "+
                "[DEFAULT GOTO <label>]"
            );
            return;
        }

        m_pointerExp = new IntVariablePointer(
            compiler, line, LRegister, m.group(1)
        );
        m_deviceNum = Expression.create(
            compiler, line, trapperRegister, "0",
            "The keyboard device number"
        );
        m_deviceAddr = Expression.create(
            compiler, line, trapperRegister, "0",
            "The address to input from"
        );
    }
}

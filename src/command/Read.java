package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * A command to read a number from a device.
 */
public class Read extends Reader {

    /**regex to extract and check arguments*/
    private final String m_intArgumentsReStr = 
        "^(" + idReStr + ")\\sfrom\\s" + expressionReStr +
        "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**regex to extract and check arguments*/
    private final String m_listArgumentsReStr = 
        "^" + listVarReStr + "\\sfrom\\s" + expressionReStr +
        "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**
     * Create a new Read instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the PRINT Command.
     */
    public Read(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);

        Matcher m;
        boolean parsed = false;

        m = Pattern.compile(m_intArgumentsReStr).matcher(arguments);
        if ( m.find()) {
            parsed = true;

            m_pointerExp = new IntVariablePointer(
                compiler, line, LRegister, m.group(1)
            );
        }

        m = Pattern.compile(m_listArgumentsReStr).matcher(arguments);
        if ( m.find()) {
            parsed = true;
            
            m_pointerExp = new ListVariablePointer(
                compiler, line, LRegister, m.group(1), m.group(2)
            );
        }

        if (! parsed) {
            compiler.error(
                line,
                "Malformed arguments to READ.\n" +
                "Usage: READ <int var> FROM <int> " +
                "{ON <errorNo> GOTO <label>} [DEFAULT GOTO <label>]\n" +
                "       READ <list var>[<int>] FROM <int> " +
                "{ON <errorNo> GOTO <label>} [DEFAULT GOTO <label>]\n" +
                "(Brackets [ ] are literal and required in above usage.)"
            );

            return;
        }

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

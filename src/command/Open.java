package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;


/**
 * A command to open a device for IO
 */
public class Open extends TrapCommand {

    /**regex to extract and check arguments*/
    private final String m_argumentsReStr = 
        "^" + expressionReStr +
        "(" + m_onReStr + "|" + m_defaultReStr + ")*$";

    /**The expression which gives the device number to open.
     * Must output to trapperRegister.
     */
    protected Expression m_deviceNum;

    /**
     * Create a new Open instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the open command.
     */
    public Open(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);

        Matcher m = Pattern.compile(m_argumentsReStr).matcher(arguments);
        if (! m.find()) {
            compiler.error(line, "Malformed arguments to OPEN.");
            return;
        }

        m_deviceNum = Expression.create(
            compiler, line, trapperRegister, m.group(1),
            "The device number to OPEN"
        );
    }

    @Override
    public String generateCode() {

        String ret = super.generateCode();
        
        //Generate the code to open the device.
        
        Trapper openTrapper = new Trapper(
            m_compiler, m_defaultLabel, m_errorMap
        );

        openTrapper.addArgument(m_deviceNum);
        openTrapper.addArgument(
            Expression.create(
                m_compiler, m_line, trapperRegister,
                ""+m_compiler.SYSCALL_OPEN, "The OPEN syscall number"
            )
        );

        ret += openTrapper.generateCode();

        return ret;
    }

    @Override
    public int stackReq() {
        return 2;
    }
}


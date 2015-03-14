package command;

import java.util.regex.*;
import java.util.*;
import pbsc.*;
import expression.*;

/**
 * A command to write an number to a device.
 */
public abstract class Writer extends Command {

    /**Automatically open and close the device as well as write.*/
    protected boolean m_autoOpen = true;

    /**Maps error integers to labels to jump to on error.*/
    private final Hashtable<Integer, String> m_errorMap;

    /**The expression which gives the device number to write to.
     * Must output to trapperRegister.
     */
    protected Expression m_deviceNum;

    /**The expression which gives the device address to write to.
     * Must output to trapperRegister.
     */
    protected Expression m_deviceAddr;

    /**The expression to write.
     * Must output to trapperRegister.
     */
    protected Expression m_output;

    /**The default label to jump to on uncaught non-success code.*/
    private final String m_defaultLabel;

    /**The regular expression to extract the 
     * ON (error const/lit) (error label)
     * statements from the arguments.
     */
    protected final String m_onReStr = 
        "\\s+on\\s*" + constLitReStr + "\\s+goto\\s+(" + idReStr + ")";

    /**The regular expression to extract the 
     * DEFAULT (error label)
     * statement from the arguments.
     */
    protected final String m_defaultReStr = "\\s+default\\s+goto\\s+(" + idReStr + ")";

    /**
     * Create a new Writer instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The string which contains multiple ON statements.
     */
    public Writer(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);
        m_errorMap = new Hashtable<Integer, String>();;

        //Extract the on statements.
        Matcher m = Pattern.compile(m_onReStr).matcher(arguments);
        while (m.find()) {
            Integer errorNo = m_compiler.constLit2Integer(m.group(1), line);
            if (errorNo == null) { continue; /*check for more errors*/ }

            if (m_errorMap.containsKey(errorNo)) {
                compiler.error(
                    line, "Multiple ON statements for error `" + errorNo + "'"
                );
            }

            String errorLabel = m.group(2);
            m_errorMap.put(errorNo, errorLabel);
        }

        //Extract the default statement (if there is one).
        m = Pattern.compile(m_defaultReStr).matcher(arguments);
        if (m.find()) {
            m_defaultLabel = m.group(1);
            if (m.find()) {
                compiler.error(line, "Multiple DEFAULT statements.");
            }
        } else {
            m_defaultLabel = null;
        }
    }

    @Override
    public void checkLabels() {
        for (String label : m_errorMap.values()) {
            Label.checkExists(m_compiler, m_line, label);
        }
    }

    @Override
    public String generateCode() {

        String ret = super.generateCode();
        

        if (m_autoOpen) {
            //Generate the code to open the device.
            
            Trapper openTrapper = new Trapper(
                m_compiler, "Open device for writing", m_defaultLabel, m_errorMap
            );

            openTrapper.addArgument(m_deviceNum);
            openTrapper.addArgument(
                Expression.create(
                    m_compiler, m_line, trapperRegister, ""+m_compiler.SYSCALL_OPEN
                )
            );

            ret += openTrapper.generateCode();
        }


        //Generate the code to write to the device
        Trapper writeTrapper = new Trapper(
            m_compiler, "Write to device", m_defaultLabel, m_errorMap
        );

        writeTrapper.addArgument(m_deviceNum);
        writeTrapper.addArgument(m_deviceAddr);
        writeTrapper.addArgument(m_output);
        writeTrapper.addArgument(
            Expression.create(
                m_compiler, m_line, trapperRegister, ""+m_compiler.SYSCALL_WRITE
            )
        );

        ret += writeTrapper.generateCode();

        if (m_autoOpen) {
            //Generate the code to close the device

            Trapper closeTrapper = new Trapper(
                m_compiler, "Close device", m_defaultLabel, m_errorMap
            );

            closeTrapper.addArgument(m_deviceNum);
            closeTrapper.addArgument(
                Expression.create(
                    m_compiler, m_line, trapperRegister, ""+m_compiler.SYSCALL_CLOSE
                )
            );
            ret += closeTrapper.generateCode();
        }

        return ret;
    }

    @Override
    public int stackReq() {
        return 4;
    }
}

package command;

import java.util.regex.*;
import java.util.*;
import pbsc.*;
import expression.*;

/**
 * A command to write a number to a device.
 */
public abstract class Writer extends TrapCommand {

    /**Automatically open and close the device as well as write.*/
    protected boolean m_autoOpen = true;

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

    /**
     * Create a new Writer instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The string which contains multiple ON statements.
     */
    public Writer(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line, arguments);
    }

    @Override
    public void generateCode() {

        super.generateCode();
        
        if (m_autoOpen) {
            //Generate the code to open the device.
            
            Trapper openTrapper = new Trapper(
                m_compiler, m_defaultLabel, m_errorMap, m_withinSub
            );

            openTrapper.addArgument(m_deviceNum);
            openTrapper.addArgument(
                Expression.create(
                    m_compiler, m_line, trapperRegister,
                    ""+m_compiler.SYSCALL_OPEN, "The OPEN syscall number"
                )
            );

            write("#TRAP to open the device");
            openTrapper.generateCode();
        }


        //Generate the code to write to the device
        Trapper writeTrapper = new Trapper(
            m_compiler, m_defaultLabel, m_errorMap, m_withinSub
        );

        writeTrapper.addArgument(m_deviceNum);
        writeTrapper.addArgument(m_deviceAddr);
        writeTrapper.addArgument(m_output);
        writeTrapper.addArgument(
            Expression.create(
                m_compiler, m_line, trapperRegister,
                ""+m_compiler.SYSCALL_WRITE, "The WRITE syscall number"
            )
        );

        if (m_autoOpen) {
            write("#TRAP to write to the device");
        }
        writeTrapper.generateCode();

        if (m_autoOpen) {
            //Generate the code to close the device

            Trapper closeTrapper = new Trapper(
                m_compiler, m_defaultLabel, m_errorMap, m_withinSub
            );

            closeTrapper.addArgument(m_deviceNum);
            closeTrapper.addArgument(
                Expression.create(
                    m_compiler, m_line, trapperRegister,
                    ""+m_compiler.SYSCALL_CLOSE, "The CLOSE syscall number"
                )
            );

            write("#TRAP to close the device");
            closeTrapper.generateCode();
        }
    }

    @Override
    public int stackReq() {
        return 4;
    }
}

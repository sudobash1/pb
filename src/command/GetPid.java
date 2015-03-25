package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * Syscall to retrieve PID.
 */
public class GetPid extends Command {

    private static final String m_reStr = "^(" + idReStr + ")$";

    /**An expression to generate a pointer to the location in memory to set.*/
    private Expression m_pointerExp = null;

    /**
     * Create a GetPid instance to retrive the current process' PID.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the GETPID command.
     */
    public GetPid(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        Matcher m = Pattern.compile(m_reStr).matcher(arguments.trim());

        if (m.find()) {
            String name = m.group(1);

            m_pointerExp = new IntVariablePointer(
                compiler, line, LRegister, name
            );

        } else {
            compiler.error(
                line,
                "Malformed arguments to GETPID.\n" +
                "Usage: GETPID <int var>"
            );
        }
    }

    @Override
    public void generateCode() {
        super.generateCode();
        m_pointerExp.generateCode();
        write("SET", tmpRegister0, m_compiler.SYSCALL_GETPID);
        write("PUSH R", tmpRegister0);
        write("TRAP");
        write("POP", tmpRegister0);
        write("SAVE", tmpRegister0, LRegister);
    }

    @Override
    public int stackReq() {
        return 0;
    }
}


package command;

import java.util.*;
import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * Insert a literal pidgen command.
 * You may use @var_name to insert the address of that variable into the
 * pidgen code.
 * You may use #const_name to insert the value of that constant into the
 * pidgen code.
 */
public class P extends Command {

    private final String m_constVarReStr = "(#|@)(" + idReStr + ")";

    private final String m_command;

    /**
     * Insert a literal pidgen command.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param command The literal pidgen command.
     */
    public P(PbscCompiler compiler, int line, String command) {
        super(compiler, line);
        m_command = command;
    }

    @Override
    public String generateCode() {

        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile(m_constVarReStr).matcher(m_command);

        while (m.find()) {

            if (m.group(1).equals("#")) {
                //Insert constant
                Integer val =
                    m_compiler.getConstantValue("#" + m.group(2), m_line);

                if (val != null) {
                    m.appendReplacement(sb, val.toString());
                }
            } else {
                //Insert variable address
                VariableDefinition vd =
                    m_compiler.getVarableDefinition(m.group(2), m_line, true);

                if (vd != null) {
                    m.appendReplacement(sb, ""+vd.getAddress());
                }
            }
        }
        m.appendTail(sb);

        return super.generateCode() +
               sb.toString() +
               endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

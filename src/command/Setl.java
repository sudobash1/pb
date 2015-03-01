package command;

import java.util.*;
import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * Assigns values to a list.
 * Values must be integer literals or constants
 */
public class Setl extends Command {

    /**An expression to generate a pointer to the location in memory to set.*/
    private Expression m_pointerExp = null;

    /**The values to save.*/
    private ArrayList<Integer> m_values = null;
 
    /**
     * Create a new Setl command.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the SETL command.
     */
    public Setl(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);
        String reStr;
        Matcher m;

        reStr = "^(" + idReStr + ")\\s*=\\s*(.*)$";
        m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(line ,"Malformed arguments to SETL.");
            return;
        }

        String name = m.group(1);
        String setExps = m.group(2).trim();

        m_pointerExp = new ListVariablePointer(
            compiler, line, LRegister, name, "0"
        );

        m_values = new ArrayList<Integer>();

        for (String exp : setExps.split("\\s")) {
            if (exp.trim().equals("")) {
                continue;
            }

            Integer val = compiler.constLit2Integer(exp, line);
            if (val == null) {
                return;
            }

            m_values.add(val);
        }

        if (m_values.size() == 0) {
            compiler.error(line, "SETL requires more arguments.");
            return;
        }

        VariableDefinition vd = compiler.getVarableDefinition(name, line);
        if (vd instanceof ListDefinition) {
            int size = ((ListDefinition)vd).getSize();

            if (m_values.size() > size) {
                compiler.error(
                    line,
                    "SETL list `" + name +"' cannot hold " + m_values.size() +
                    " items."
                );
                return;
            }
        }
    }

    @Override
    public String generateCode() {
        StringBuilder sb = new StringBuilder();
        sb.append(m_pointerExp.generateCode());

        sb.append("SET R" + tmpRegister1 + " 1");

        for (int x = 0; x < m_values.size() - 1; ++x) {
            sb.append("SET R" + RRegister + " " + m_values.get(x));
            sb.append(m_compiler.lineEnding());
            sb.append("SAVE R" + RRegister + " R" + LRegister);
            sb.append(m_compiler.lineEnding());
            sb.append("ADD R" + LRegister + " R" + LRegister + " R" + tmpRegister1);
            sb.append(m_compiler.lineEnding());
        }

        sb.append("SET R" + RRegister + " " + m_values.get(m_values.size()-1));
        sb.append(m_compiler.lineEnding());
        sb.append("SAVE R" + RRegister + " R" + LRegister);
        sb.append(m_compiler.lineEnding());

        return sb.toString();
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

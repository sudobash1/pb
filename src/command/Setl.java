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

        VariableDefinition vd =
            compiler.getVarableDefinition(name, line, true);

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

        String ret = super.generateCode();

        ret += 
            m_pointerExp.generateCode() +
            "SET R" + tmpRegister1 + " 1" + endl();

        for (int x = 0; x < m_values.size() - 1; ++x) {
            ret +=
                "SET R" + RRegister + " " + m_values.get(x) + endl();
                "SAVE R" + RRegister + " R" + LRegister + endl();
                "ADD R" + LRegister + " R" + LRegister + " R" + tmpRegister1 +
                endl();
        }

        ret += 
            "SET R" + RRegister + " " + m_values.get(m_values.size()-1) +
            endl() +
            "SAVE R" + RRegister + " R" + LRegister + endl();

        return ret;
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

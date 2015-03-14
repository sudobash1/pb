package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

/**
 * Assigns a value to a variable.
 * Works with both LISTs and INTs.
 */
public class Set extends Command {

    /**An expression to generate a pointer to the location in memory to set.*/
    private Expression m_pointerExp = null;

    /**An expression to generate the value to save.*/
    private Expression m_valueExp = null;
 
    /**
     * Create a new Set command.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments to the SET command.
     */
    public Set(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);
        String reStr;
        Matcher m;

        //Assign to simple variable
        reStr = "^(" + idReStr + ")\\s*=\\s*" + expressionReStr + "$";
        m = Pattern.compile(reStr).matcher(arguments);

        if (m.find()) {
            String name = m.group(1);
            String setExp = m.group(2);

            m_pointerExp = new IntVariablePointer(
                compiler, line, LRegister, name
            );
            m_valueExp = Expression.create(compiler, line, RRegister, setExp);

            return;
        }

        //Assign to list variable
        reStr = "^" + listVarReStr + "\\s*=\\s*" + expressionReStr + "$";
        m = Pattern.compile(reStr).matcher(arguments);

        if (m.find()) {
            String name = m.group(1);
            String indexExpr = m.group(2);
            String setExp = m.group(3);

            m_pointerExp = new ListVariablePointer(
                compiler, line, LRegister, name, indexExpr
            );
            m_valueExp = Expression.create(
                compiler, line, RRegister, setExp
            );

            return;
        }

        compiler.error(line ,"Malformed arguments to set.");
        return;
    }

    @Override
    public String generateCode() {
        String getLValue = m_pointerExp.generateCode();
        String getRValue = m_valueExp.generateCode();
        return super.generateCode() + 
               getLValue + getRValue +
               "SAVE R" + RRegister + " R" + LRegister + 
               endl();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

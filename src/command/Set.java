package command;

import java.util.regex.*;
import pbsc.*;
import expression.*;

public class Set extends Command {

    /**An expression to generate a pointer to the location in memory to set.*/
    private Expression m_pointerExp = null;

    /**An expression to generate the value to save.*/
    private Expression m_valueExp = null;

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
        return ""; //XXX
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

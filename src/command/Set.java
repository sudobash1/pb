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

        //For now we are just going to worry about Int vars and const/lit values.
        String reStr = "^(" + idReStr + ")\\s*=\\s*" + expressionReStr + "$";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(line ,"Malformed arguments to set.");
            return;
        }

        String name = m.group(1);
        String setExp = m.group(2);

        m_pointerExp = new IntVariablePointer(compiler, line, name);
        m_valueExp = Expression.create(compiler, line, setExp);
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

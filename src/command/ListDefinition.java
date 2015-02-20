package command;

import java.util.regex.*;
import pbsc.*;

public class ListDefinition extends VariableDefinition {

    int m_size = 1;

    public ListDefinition(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        String reStr = "^(" + idReStr + ")\\s*\\[\\s*" + constLitReStr + "\\s*\\]$";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(line ,"Malformed argument to List.");
            return;
        }

        String name = m.group(1);
        String size = m.group(2);

        compiler.registerNewVariable(name, this, line);

        Integer IntegerSize = compiler.constLit2Integer(size, line);
        if (IntegerSize == null) return;

        m_size = IntegerSize;
    }

    @Override
    public String generateCode() {
        //No code needs to be executed in actual Pidgin.
        //The variable is bound at compile time.
        return "";
    }

    @Override
    public int pidgenInstructionsNeeded() {
        return 0;
    }

    @Override
    public int getSize() {
        return m_size;
    }
}

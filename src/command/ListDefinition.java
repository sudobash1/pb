package command;

import java.util.regex.*;
import pbsc.*;

/**
 * A command to generate a LIST variable.
 * This command generates no pidgen code, but it registers a new LIST variable
 * with the PbscCompiler instance.
 * @see PbscCompiler.registerNewVariable
 */
public class ListDefinition extends VariableDefinition {

    /**The number of elements this list can hold.*/
    private int m_size = 1;

    /**
     * Register the list.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments given the LIST command.
     */
    public ListDefinition(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        String reStr =
            "^(" + idReStr + ")\\s*\\[\\s*" + constLitReStr + "\\s*\\]$";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(
                line,
                "Malformed argument to LIST.\n" +
                "Usage: LIST <list var> [ <const or lit> ]\n"+
                "(Brackets [ ] are literal and required in above usage.)"
            );
            return;
        }

        String name = m.group(1);
        String size = m.group(2);

        if (compiler.isReservedWord(name)) {
            compiler.error(line, "Illegal name for list `" + name + "'.");
            return;
        }

        compiler.registerNewVariable(name, this, line);

        Integer IntegerSize = compiler.constLit2Integer(size, line);
        if (IntegerSize == null) return;
        if (IntegerSize <= 0) {
            compiler.error(
                line,
                "Lists must have length at least 1." +
                " Found `" + IntegerSize + "'."
            );
        }

        m_size = IntegerSize;
    }

    @Override
    public int getSize() {
        return m_size;
    }

    @Override
    public int stackReq() {
        return 0;
    }

    @Override
    public String pushVar() {
        String ret = "";
        for (int i = 0; i < m_size; ++i) {
            ret += 
                "SET R" + tmpRegister0 + " " + (getAddress() + i) + endl() +
                "LOAD R" + tmpRegister0 + " R" + tmpRegister0 + endl() +
                "PUSH R" + tmpRegister0 + endl();
        }
        return ret;
    }

    @Override
    public String popVar() {
        String ret = "";
        for (int i = 0; i < m_size; ++i) {
            ret += 
                "POP R" + tmpRegister1 + endl() +
                "SET R" + tmpRegister0 + " " + (getAddress() + i) + endl() +
                "SAVE R" + tmpRegister1 + " R" + tmpRegister0 + endl();
        }
        return ret;
    }

}

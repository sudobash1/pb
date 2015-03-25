package command;

import java.util.regex.*;
import pbsc.*;

/**
 * A command to generate a INT variable.
 * This command generates no pidgen code, but it registers a new INT variable
 * with the PbscCompiler instance.
 * @see PbscCompiler.registerNewVariable
 */
public class IntDefinition extends VariableDefinition {

    /**
     * Create the integer without registering.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public IntDefinition(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    /**
     * Register the integer.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The arguments given the INt command.
     */
    public IntDefinition(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        String reStr = "^(" + idReStr + ")$";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            if (arguments.contains("=")) {
                compiler.error(
                    line,
                    "Assignment durring variable declaration not supported."
                );
            } else {
                compiler.error(
                    line,
                    "Malformed argument to INT.\n" +
                    "Usage: INT <int var>"
                );
            }
            return;
        }

        String name = m.group(1);

        if (compiler.isReservedWord(name)) {
            compiler.error(line, "Illegal name for int `" + name + "'.");
            return;
        }

        compiler.registerNewVariable(name, this, line);
    }

    @Override
    public int stackReq() {
        return 0;
    }

    @Override
    public int getSize() {
        return 1; //Ints take one byte
    }

    @Override
    public void pushVar() {
        write("SET", tmpRegister0, ""+getAddress());
        write("LOAD", tmpRegister0, tmpRegister0);
        write("PUSH", tmpRegister0);
    }

    @Override
    public void popVar() {
        write("POP", tmpRegister1);
        write("SET", tmpRegister0, ""+getAddress());
        write("SAVE", tmpRegister1, tmpRegister0);
    }
}

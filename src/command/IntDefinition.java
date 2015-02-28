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
            compiler.error(line ,"Malformed argument to Int.");
            return;
        }

        String name = m.group(1);

        compiler.registerNewVariable(name, this, line);
    }

    @Override
    public String generateCode() {
        //No code needs to be executed in actual Pidgin.
        //The variable is bound at compile time.
        return "";
    }

    @Override
    public int stackReq() {
        return 0;
    }

    @Override
    public int getSize() {
        return 1; //Ints take one byte
    }
}

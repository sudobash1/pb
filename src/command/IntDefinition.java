package command;

import java.util.regex.*;
import pbsc.*;

public class IntDefinition extends VariableDefinition {

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
    public int getSize() {
        return 1;
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

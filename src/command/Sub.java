package command;

import java.util.regex.*;
import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Start a subroutine block.
 */
public class Sub extends Command {

    public final String label;
    public final String endLabel;

    protected final ArrayList<VariableDefinition> localVars;

    /**
     * Create a Sub instance to start a subroutine block.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The subroutine name.
     */
    public Sub(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);

        localVars = new ArrayList<VariableDefinition>();

        String reStr = "^(" + idReStr + ")$";
        Matcher m = Pattern.compile(reStr).matcher(arguments);

        if (! m.find()) {
            compiler.error(
                line,
                "Malformed argument to SUB.\n" +
                "Usage: SUB <sub name>"
            );
            label = "";
            endLabel = "";
            return;
        }

        String name = m.group(1);

        if (compiler.isReservedWord(name)) {
            compiler.error(line, "Illegal name for subroutine `"+name+"'.");
            label = "";
            endLabel = "";
            return;
        }

        label = compiler.applyMagic("SUBSTART"+name);
        endLabel = compiler.applyMagic("SUBEND"+name);

        compiler.registerNewSub(name, this, line);
        compiler.pushScope(label);

        currentSub = this;
    }

    /**
     * Generates the code to call this subroutine.
     * If it is being called from within a subroutine (including itself), set
     * fromWithinSub to the instance of the calling Sub
     * @param fromWithinSub The instance of the calling Sub (or null).
     * @return The pidgen code to call the subroutine.
     */
    public String execute(Sub fromWithinSub) {
        String ret = "";

        if (fromWithinSub != null) {
            //We are being called from within a sub so we must store it's
            //local varables to the stack.
            ret += fromWithinSub.storeVariables();
        }

        ret +=
            "PUSH R" + pcRegister + endl() +
            "BRANCH " + label + endl();

        if (fromWithinSub != null) {
            //restore local varables from the stack.
            ret += fromWithinSub.restoreVariables();
        }

        return ret;
    }

    /**
     * Generates the code to store this subroutine's variables to the stack.
     * @return The pidgen code to store this subroutine's variables to the
     * stack.
     */
    private String storeVariables() {
        String ret = "# Saving local variables:" + endl();
        for (VariableDefinition vd : localVars) {
            ret += vd.pushVar();
        }
        return ret;
    }

    /**
     * Generates the code to restore this subroutine's variables from the
     * stack.
     * @return The pidgen code to restore this subroutine's variables from the
     * stack.
     */
    private String restoreVariables() {
        String ret = "# Restoring local variables:" + endl();
        for (int i = localVars.size() - 1; i >= 0; --i) {
            ret += localVars.get(i).popVar();
        }
        return ret;
    }

    @Override
    public String generateCode() {
        return super.generateCode() +
               "BRANCH " + endLabel + endl() +
               ":" + label + endl();
    }

    @Override
    public void checkLabels() {
        if (currentSub != null) {
            m_compiler.error( currentSub.m_line, "Unterminated SUB");
            currentSub = null;
        }
    }

    @Override
    public int stackReq() {
        return 0;
    }
}

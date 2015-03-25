package command;

import java.util.regex.*;
import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Start a subroutine block.
 */
public class Sub extends Command {

    public final String name;
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
        Label.createLocalLabelTable(this);

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
            name = "";
            return;
        }

        name = m.group(1);

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
     */
    public void execute(Sub fromWithinSub) {
        if (fromWithinSub != null) {
            //We are being called from within a sub so we must store it's
            //local varables to the stack.
            fromWithinSub.storeVariables();
        }

        write("PUSH", PbscCompiler.pcRegister);
        write("BRANCH", label);

        if (fromWithinSub != null) {
            //restore local varables from the stack.
            fromWithinSub.restoreVariables();
        }
    }

    /**
     * Generates the code to store this subroutine's variables to the stack.
     * @return The pidgen code to store this subroutine's variables to the
     * stack.
     */
    private void storeVariables() {
        write("# Saving local variables:");
        for (VariableDefinition vd : localVars) {
            vd.pushVar();
        }
    }

    /**
     * Generates the code to restore this subroutine's variables from the
     * stack.
     * @return The pidgen code to restore this subroutine's variables from the
     * stack.
     */
    private void restoreVariables() {
        write("# Restoring local variables:");
        for (int i = localVars.size() - 1; i >= 0; --i) {
            localVars.get(i).popVar();
        }
    }

    @Override
    public void generateCode() {
        super.generateCode();
        write("BRANCH", endLabel);
        write(":" + label);
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

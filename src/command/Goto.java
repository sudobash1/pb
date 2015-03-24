package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Jump to a label.
 */
public class Goto extends Command {

    /**The label to jump to.*/
    private String m_label;

    /**The subroutine this goto was found in. May be null.*/
    private Sub m_withinSub;

    /**
     * jump to a label
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param label The label (without scope mangling) to jump to.
     */
    public Goto(PbscCompiler compiler, int line, String label) {
        super(compiler, line);

        m_label = label.trim();

        if (! label.matches(idReStr)) {
            m_compiler.error(
                line, "Invalid label name `" + label + "'"
            );
            return;
        }

        m_withinSub = currentSub;
    }

    @Override
    public void checkLabels() {
        Label.retriveLabel(m_compiler, m_line, m_label, m_withinSub);
    }

    @Override
    public String generateCode() {
        String ret = super.generateCode();

        Label label = 
            Label.retriveLabel(m_compiler, m_line, m_label, m_withinSub);

        //Clear the stack if leaving a subroutine.
        if (m_withinSub != null && !label.inSub()) {
            ret += m_compiler.callRuntimeMethod(
                PbscCompiler.RunTimeLibrary.CLEAR_STACK
            );
        }

        ret += "BRANCH " + label.text() + endl();

        return ret;
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

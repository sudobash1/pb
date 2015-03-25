package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Execute a subroutine.
 */
public class Gosub extends Command {

    /**The subroutine name to jump to.*/
    private final String m_subName;

    /**The subroutine to jump to.*/
    private Sub m_sub = null;

    /**If we were within a subroutine, then this is a reference
     * to the Sub instance. Null otherwise.
     */
    protected final Sub m_currentSub;

    /**
     * Create an instance of Gosub to execute a subroutine.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param subroutine The subroutine to execute.
     */
    public Gosub(PbscCompiler compiler, int line, String subroutine) {
        super(compiler, line);

        subroutine = subroutine.trim();

        if (! subroutine.matches(idReStr)) {
            m_compiler.error(
                line, "Invalid subroutine name `" + subroutine + "'"
            );
            m_subName = null;
            m_currentSub = null;
            return;
        }

        m_subName = subroutine;
        m_currentSub = currentSub;
    }

    @Override
    public void checkLabels() {
        if (m_subName != null) {
            m_sub = m_compiler.getSub(m_subName, m_line);
        }
    }

    @Override
    public void generateCode() {
        super.generateCode();
        m_sub.execute(m_currentSub);
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Starts an IF block
 */
public class If extends Command {

    /**The number of IF blocks found so far.*/
    private static int ifNumber = 0;

    /**The stack of IF block beginings.*/
    private static Stack<If> ifBlocks = new Stack<If>();

    /**The expression to test*/
    private Expression m_exp = null;

    /**Is there an associated ELSE for this IF?*/
    protected boolean m_foundElse = false;

    /**Are there multiple ELSE for this IF. An error condition.*/
    protected boolean m_foundMultElse = false;

    protected final String blockID;
    protected final String thenLabel;
    protected final String elseLabel;
    protected final String endLabel;

    /**
     * Create a new IF block.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param exp The expression to test.
     */
    public If(PbscCompiler compiler, int line, String exp) {
        super(compiler, line);

        m_exp = Expression.create(compiler, line, ifRegister, exp);

        blockID = "IF"+ifNumber;
        thenLabel = compiler.applyMagic("THEN"+ifNumber);
        elseLabel = compiler.applyMagic("ELSE"+ifNumber);
        endLabel = compiler.applyMagic("FI"+ifNumber);
        
        ++ifNumber;

        compiler.pushScope(blockID);
        ifBlocks.push(this);
    }

    @Override
    public void checkLabels() {
        while (! ifBlocks.empty()){
            If ifBlock = ifBlocks.pop();
            m_compiler.error( ifBlock.m_line, "Unterminated IF");
        }
    }

    /**
     * Return the If instance for the current IF block.
     * @param exit If exit is true, then exit the current IF block also.
     * @return Current if instance, or null if not in an IF block.
     */
    public static If currentIf(boolean exit) {
        if (ifBlocks.empty()){
            return null;
        }
        return exit ? ifBlocks.pop() : ifBlocks.peek();
    }

    @Override
    public String generateCode() {
        String ret = super.generateCode() + 
                     m_exp.generateCode() +
                     "SET R" + tmpRegister1 + " -1" + endl() +
                     "BLT R" + tmpRegister1 + " R" + ifRegister + " " +
                     thenLabel + endl();
        if (m_foundElse) {
            ret += "BRANCH " + elseLabel + endl();
        } else {
            ret += "BRANCH " + endLabel + endl();
        }
        ret += ":" + thenLabel + endl();
        return ret;
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

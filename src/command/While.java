package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Starts a while block
 */
public class While extends Command {

    /**The number of while blocks found so far.*/
    private static int whileNumber = 0;

    /**The stack of WHILE block beginings.*/
    private static Stack<While> whileBlocks = new Stack<While>();

    /**The expression to test*/
    private Expression m_exp = null;

    protected final String blockID;
    protected final String startLabel;
    protected final String doneLabel;

    /**
     * Create a new WHILE block.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param exp The expression to test.
     */
    public While(PbscCompiler compiler, int line, String exp) {
        super(compiler, line);

        m_exp = Expression.create(compiler, line, whileRegister, exp);

        blockID = "WHILE"+whileNumber;
        startLabel = "START"+whileNumber;
        doneLabel = "DONE"+whileNumber;

        ++whileNumber;

        compiler.pushScope(blockID);
        whileBlocks.push(this);
    }

    /**
     * Return the While instance for the current WHILE block and exit it.
     * @return Current if instance, or null if not in a WHILE block.
     */
    public static While currentWhile() {
        return whileBlocks.pop();
    }

    @Override
    public String generateCode() {
        return super.generateCode() + 
               m_exp.generateCode() +
               "SET R" + tmpRegister1 + " 0" +
               m_compiler.lineEnding() +
               "BNE R" + tmpRegister1 + " R" + whileRegister + startLabel +
               m_compiler.lineEnding() +
               "BRANCH " + doneLabel +
               m_compiler.lineEnding() +
               ":" + startLabel +
               m_compiler.lineEnding();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

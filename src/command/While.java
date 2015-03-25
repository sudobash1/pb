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
    protected Expression m_exp = null;

    protected final String blockID;
    protected final String testLabel;
    protected final String startLabel;
    protected final String doneLabel;

    /**
     * This command gets run before the loop starts.
     * It may be null.*/
    protected Command m_preCommand = null;
    /**
     * This command gets run after every itteration.
     * It may be null.*/
    protected Command m_postCommand = null;

    /**
     * Create a new WHILE block.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param exp The expression to test.
     */
    public While(PbscCompiler compiler, int line, String exp) {
        super(compiler, line);

        if (! (this instanceof For)) {
            m_exp = Expression.create(compiler, line, whileRegister, exp);
        }

        blockID = "WHILE"+whileNumber;
        testLabel = compiler.applyMagic("WHILE"+whileNumber);
        startLabel = compiler.applyMagic("START"+whileNumber);
        doneLabel = compiler.applyMagic("DONE"+whileNumber);

        ++whileNumber;

        compiler.pushScope(blockID);
        whileBlocks.push(this);
    }

    /**
     * Return the While instance for the current WHILE block and exit it.
     * @param exit If exit is true, then exit the current WHILE block also.
     * @return Current if instance, or null if not in a WHILE block.
     */
    public static While currentWhile(boolean exit) {
        if (whileBlocks.empty()){
            return null;
        }
        return exit ? whileBlocks.pop() : whileBlocks.peek();
    }

    /**
     * Return the command to be run after every itteration.
     * @return the command to be run right after every itteration.
     */
    public Command postCommand() {
        return m_postCommand;
    }

    @Override
    public void generateCode() {
        super.generateCode();

        //Initialize the loop
        if (m_preCommand != null) {
            if (m_compiler.debugging()) {
                write("# Initializing loop");
            }
            m_preCommand.generateCode();
            if (m_compiler.debugging()) {
                write("# Start of loop");
            }
        }

        if (m_compiler.debugging()) {
            write("# Test if loop is finished");
        }

        //Label for start of loop
        write(":" + testLabel);
        m_exp.generateCode();
        write("SET", tmpRegister1, " -1");
        write("BLT", tmpRegister1, whileRegister, startLabel);
        write("BRANCH", doneLabel);
        write(":" + startLabel);

        if (m_compiler.debugging()) {
            write("# Loop body");
        }
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

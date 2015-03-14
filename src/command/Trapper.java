package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * A helper class which generates code to push data, execute a trap, and 
 * examine the return status code.
 */
public class Trapper {

    /**The number of Trappers created so far.*/
    private static int trapperNumber = 0;

    /**This writer's writerNumber*/
    private final int m_trapperNumber;

    /**The main instance of the PbscCompiler.*/
    private final PbscCompiler m_compiler;

    /**Expressions to generate values to push for trap*/
    private ArrayList<Expression> m_params;

    /**Maps error integers to labels to jump to on error.*/
    private final Hashtable<Integer, String> m_errorMap;

    /**The default label to jump to on uncaught non-success code.*/
    private final String m_defaultLabel;

    /**The label to jump to on success*/
    private final String m_successLabel;

    /**The label to jump to to check non-success code.*/
    private String m_errorLabel;

    /**
     * Create a new Trapper instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param defaultLabel The default label to jump to on uncaught error.
     * Note: may be null.
     * @param errorMap Maps error integers to labels to jump to on error.
     * Note may be null.
     */
    public Trapper(
        PbscCompiler compiler, String defaultLabel,
        Hashtable<Integer, String> errorMap
    ) {
        m_compiler = compiler;
        m_params = new ArrayList<Expression>();
        m_defaultLabel = defaultLabel;
        m_errorMap = errorMap;

        m_trapperNumber = trapperNumber;
        ++trapperNumber;

        m_successLabel = compiler.applyMagic("TRAPPSUCCESS" + trapperNumber);
        m_errorLabel = compiler.applyMagic("TRAPPERERROR" + trapperNumber);
    }

    /**
     * Return the line ending.
     */
    public final String endl() {
        return m_compiler.lineEnding();
    }

    /**
     * Add argument.
     * Arguments will be pushed to the stack in the order that they were
     * added.
     * 
     * Argument must output to trapperRegister.
     *
     * @param expr Expression to push to the stack.
     */
    public void addArgument(Expression expr) {
        m_params.add(expr);
    }

    /**
     * Generate the pidgen code for this trapper.
     */
    public String generateCode() {
        String ret = "";

        //Push the arguments
        for (Expression expr : m_params) {
            ret +=
                expr.generateCode() +
                "PUSH R" + Command.trapperRegister;
            if (m_compiler.debugging() && expr.m_comment != null) {
                ret += "   #" + expr.m_comment;
            }
            ret += endl();
        }

        //Execute and get error code
        ret += "TRAP";
        if (m_compiler.debugging()) {
            ret += "   #Execute trap and examine return value";
        }
        ret +=
            endl() +
            "POP R" + Command.tmpRegister1 + endl();

        //If we don't need to check the error code, then we are done.
        if (m_errorMap.size() == 0 && m_defaultLabel == null) {
            return ret;
        }

        //We just have the default handler
        if (m_errorMap.size() == 0) {
            ret +=
                //Branch to default if error code != 0
                "SET R" + Command.tmpRegister2 + " 0" + endl() +
                "BNE R" + Command.tmpRegister1 + " R" + Command.tmpRegister2 + " " + m_defaultLabel +
                endl();
            return ret;
        }

        //Check the error code
        ret +=
            //Branch to success if error code = 0
            "SET R" + Command.tmpRegister2 + " 0" + endl() +
            "BNE R" + Command.tmpRegister1 + " R" + Command.tmpRegister2 + " " + m_errorLabel +
            endl() +
            "BRANCH " + m_successLabel + endl() +
            ":" + m_errorLabel + endl();

        //Check all the errors.
        for (Map.Entry<Integer, String> error : m_errorMap.entrySet()) {
            String skipErrorLabel = m_compiler.applyMagic(
                "TRAPPER" + trapperNumber  + "SKIP" + error.getKey()
            );
            ret +=
                "SET R" + Command.tmpRegister2 + " " + error.getKey() + 
                endl() +
                "BNE R" + Command.tmpRegister1 + " R" + Command.tmpRegister2 + " " + skipErrorLabel +
                endl() +
                "BRANCH " + error.getValue() + endl() +
                ":" + skipErrorLabel + endl();
        }

        //Jump to the default label if there is on.
        if (m_defaultLabel != null) {
            ret += "BRANCH " + m_defaultLabel + endl();
        }

        //Jump here if there was no error.
        ret += ":" + m_successLabel + endl();
        
        return ret;
    }

    public int stackReq() {
        return m_params.size();
    }
}

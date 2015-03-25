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

    /**The subroutine this goto was found in. May be null.*/
    private Sub m_withinSub;

    /**
     * Create a new Trapper instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param defaultLabel The default label to jump to on uncaught error.
     * Note: may be null.
     * @param errorMap Maps error integers to labels to jump to on error.
     * May be null.
     * @param currentSub The sub this Trapper was created in. May be null.
     */
    public Trapper(
        PbscCompiler compiler, String defaultLabel,
        Hashtable<Integer, String> errorMap, 
        Sub currentSub
    ) {
        m_compiler = compiler;
        m_params = new ArrayList<Expression>();
        m_defaultLabel = defaultLabel;
        m_errorMap = errorMap;
        m_withinSub = currentSub;

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
    public void generateCode() {
        //Push the arguments
        for (Expression expr : m_params) {
            expr.generateCode();
            if (m_compiler.debugging() && expr.m_comment != null) {
                m_compiler.write(
                    "PUSH", Command.trapperRegister, "   #", expr.m_comment
                );
            } else {
                m_compiler.write("PUSH", Command.trapperRegister);
            }

        }

        //Execute and get error code
        if (m_compiler.debugging()) {
            m_compiler.write(
                "TRAP", "   #Execute trap and examine return value"
            );
        } else {
            m_compiler.write("TRAP");
        }

        m_compiler.write("POP", Command.tmpRegister1);

        //If we don't need to check the error code, then we are done.
        if (m_errorMap.size() == 0 && m_defaultLabel == null) {
            return;
        }

        //Check the error code
        
        //Branch to success if error code = 0
        m_compiler.write("SET", Command.tmpRegister2, "0");
        m_compiler.write(
            "BNE", Command.tmpRegister1, Command.tmpRegister2, m_errorLabel
        );
        m_compiler.write("BRANCH", m_successLabel);
        m_compiler.write(":" + m_errorLabel);

        //Check for all the errors.
        for (Map.Entry<Integer, String> error : m_errorMap.entrySet()) {

            Label label = Label.retriveLabel(
                m_compiler, -1, error.getValue(), m_withinSub
            );

            String skipErrorLabel = m_compiler.applyMagic(
                "TRAPPER" + trapperNumber  + "SKIP" + error.getKey()
            );

            m_compiler.write(
                "SET", Command.tmpRegister2, error.getKey().toString()
            );
            m_compiler.write(
                "BNE", Command.tmpRegister1, Command.tmpRegister2,
                skipErrorLabel
            );

            //Clear the stack if leaving a subroutine.
            if (m_withinSub != null && !label.inSub()) {
                m_compiler.callRuntimeMethod(
                    PbscCompiler.RunTimeLibrary.CLEAR_STACK
                );
            }

            m_compiler.write("BRANCH", label.text());
            m_compiler.write(":" + skipErrorLabel);
        }

        //Jump to the default label if there is one.
        if (m_defaultLabel != null) {

            Label label = Label.retriveLabel(
                m_compiler, -1, m_defaultLabel, m_withinSub
            );

            //Clear the stack if leaving a subroutine.
            if (m_withinSub != null && !label.inSub()) {
                m_compiler.callRuntimeMethod(
                    PbscCompiler.RunTimeLibrary.CLEAR_STACK
                );
            }

            m_compiler.write("BRANCH", m_defaultLabel);
        }

        //Jump here if there was no error.
        m_compiler.write(":" + m_successLabel);
    }

    public int stackReq() {
        return m_params.size();
    }
}

package expression;

import java.util.*;
import pbsc.*;

/**
 * A command which performs a logical AND opperation on two expressions.
 * negative numbers evaluate to false. 0 and positive numbers are true.
 */
public class And extends LispExpression {

    /**The number of ANDs found so far.*/
    private static int andNumber = 0;

    protected final String m_successLabel;
    protected final String m_failLabel;

    /**
     * Create an And LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * logical AND.
     */
    public And(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (AND). Requires 2, found " +
                operands.size() + "."
            );
        }

        m_successLabel = compiler.applyMagic("ANDSUCCESS"+andNumber);
        m_failLabel = compiler.applyMagic("ANDFAIL"+andNumber);
        ++andNumber;
    }

    @Override
    public String generateCode() {
        return
            m_operands.get(0).generateCode() +
            "SET R" + tmpRegister1 + " 0" + endl() +
            "BLT R" + m_register + " R" + tmpRegister1 + " " + m_failLabel +
            endl() +
            m_operands.get(1).generateCode() +
            "BRANCH " + m_successLabel + endl() +
            ":" + m_failLabel + endl() +
            "SET R" + m_register + " -1" + endl() +
            ":" + m_successLabel + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

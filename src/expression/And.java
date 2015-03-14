package expression;

import java.util.*;
import pbsc.*;

public class And extends LispExpression {

    /**The register the first value to add stored.*/
    private final int m_tmpRegister;

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

        m_tmpRegister = (register == tmpRegister1)? tmpRegister2: tmpRegister1;
        
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
            "SET R" + m_tmpRegister + " 0" + endl() +
            "BNE R" + m_register + " R" + m_tmpRegister + " " +
            m_successLabel + endl() +
            "SET R" + m_register + " 0" + endl() +
            "BRANCH " + m_failLabel + endl() +
            ":" + m_successLabel + endl() +
            m_operands.get(1).generateCode() +
            ":" + m_failLabel + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

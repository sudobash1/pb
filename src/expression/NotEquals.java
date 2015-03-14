package expression;

import java.util.*;
import pbsc.*;

public class NotEquals extends LispExpression {

    private static int notEqualsCounter = 0;

    /**The register the first value to test stored.*/
    private final int m_tmpRegister;

    private final String m_successLabel;
    private final String m_failLabel;

    /**
     * Create a NotEquals LispExpression instance.
     * It evaluates to 1 on true or 0 on false.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * compare.
     */
    public NotEquals(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        m_tmpRegister = (register == tmpRegister1) ? tmpRegister2 : tmpRegister1;
        
        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (!=). Requires 2, found " +
                operands.size() + "."
            );
        }

        m_successLabel = compiler.applyMagic("NESUCCESS" + notEqualsCounter);
        m_failLabel = compiler.applyMagic("NEFAIL" + notEqualsCounter);

        ++notEqualsCounter;
    }

    @Override
    public String generateCode() {
        return m_operands.get(0).generateCode() +
               "COPY R" + m_tmpRegister + " R" + m_register +
               endl() +
               m_operands.get(1).generateCode() +
               "BNE R" + m_register + " R" + m_tmpRegister + " " + m_successLabel +
               endl() +
               "SET R" + m_register + " 0" + //Failed
               endl() +
               "BRANCH " + m_failLabel +
               endl() +
               ":" + m_successLabel +
               endl() +
               "SET R" + m_register + " 1" + //Success
               endl() +
               ":" + m_failLabel +
               endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

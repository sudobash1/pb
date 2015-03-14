package expression;

import java.util.*;
import pbsc.*;

public class Or extends LispExpression {

    /**The register the first value to add stored.*/
    private final int m_tmpRegister;

    /**The number of ORs found so far.*/
    private static int orNumber = 0;

    protected final String m_successLabel;

    /**
     * Create an Or LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * logical OR.
     */
    public Or(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        m_tmpRegister = (register == tmpRegister1)? tmpRegister2: tmpRegister1;
        
        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (OR). Requires 2, found " +
                operands.size() + "."
            );
        }

        m_successLabel = compiler.applyMagic("ORSUCCESS"+orNumber);
        ++orNumber;
    }

    @Override
    public String generateCode() {
        return
            m_operands.get(0).generateCode() +
            "SET R" + m_tmpRegister + " 0" + endl() +
            "BNE R" + m_register + " R" + m_tmpRegister + " " +
            m_successLabel + endl() +
            m_operands.get(1).generateCode() +
            ":" + m_successLabel + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

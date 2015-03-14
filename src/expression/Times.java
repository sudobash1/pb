package expression;

import java.util.*;
import pbsc.*;

public class Times extends LispExpression {

    /**The register the first value to add stored.*/
    private final int m_tmpRegister;

    /**
     * Create a Times LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * multiply.
     */
    public Times(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        m_tmpRegister = (register == tmpRegister1)? tmpRegister2: tmpRegister1;
        
        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (*). Requires 2, found " +
                operands.size() + "."
            );
        }
    }

    @Override
    public String generateCode() {
        return m_operands.get(0).generateCode() +
               "COPY R" + m_tmpRegister + " R" + m_register +
               endl() +
               m_operands.get(1).generateCode() +
               "MULT R" + m_register + " R" + m_tmpRegister  + " R" +
               m_register +
               endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

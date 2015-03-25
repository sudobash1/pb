package expression;

import java.util.*;
import pbsc.*;

/**
 * A command which performs a logical OR opperation on two expressions.
 * negative numbers evaluate to false. 0 and positive numbers are true.
 */
public class Or extends LispExpression {

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
        PbscCompiler compiler, int line, String register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

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
    public void generateCode() {
        m_operands.get(0).generateCode();
        write("SET", tmpRegister1, "-1");
        write("BLT", tmpRegister1, m_register, m_successLabel);
        m_operands.get(1).generateCode();
        write(":" + m_successLabel);
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

package expression;

import java.util.*;
import pbsc.*;

/**
 * A command which tests if one number is less than or equal to another.
 * Evaluates to a negative number for to false.
 * 0 or a positive numbers means true.
 */
public class LessEquals extends LispExpression {

    /**
     * Create a LessEquals LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * compare.
     */
    public LessEquals(
        PbscCompiler compiler, int line, String register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (<=). Requires 2, found " +
                operands.size() + "."
            );
        }
    }

    @Override
    public void generateCode() {
        m_operands.get(0).generateCode();
        write("PUSH", m_register);
        m_operands.get(1).generateCode();
        write("POP", tmpRegister1);
        write("# Testing", tmpRegister1, " <= ", m_register);
        write("SUB", m_register, m_register,tmpRegister1);
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

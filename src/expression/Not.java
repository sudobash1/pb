package expression;

import java.util.*;
import pbsc.*;

/**
 * An expression which inverts a bool value.
 * Evaluates to a negative number for to false.
 * 0 or a positive numbers means true.
 */
public class Not extends LispExpression {

    /**
     * Create a Not LispExpression instance.
     * It evaluates to a non-negative number on true.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing one value to invert.
     */
    public Not(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        if (operands.size() != 1) {
            compiler.error(
                line,
                "Wrong number of arguments to (not). Requires 1, found " +
                operands.size() + "."
            );
        }
    }

    @Override
    public String generateCode() {
        return m_operands.get(0).generateCode() +
               "SET R" + tmpRegister1 + " -1" + endl() +
               "SUB R" + m_register + " R" + m_register + " R" +
               tmpRegister1 + endl() +
               "MUL R" + m_register + " R" + m_register + " R" +
               tmpRegister1 + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

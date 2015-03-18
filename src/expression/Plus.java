package expression;

import java.util.*;
import pbsc.*;

/**An expression which adds two numbers.*/
public class Plus extends LispExpression {

    /**
     * Create a Plus LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * add.
     */
    public Plus(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (+). Requires 2, found " +
                operands.size() + "."
            );
        }
    }

    @Override
    public String generateCode() {
        return m_operands.get(0).generateCode() +
               "COPY R" + tmpRegister1 + " R" + m_register + endl() +
               m_operands.get(1).generateCode() +
               "ADD R" + m_register + " R" + m_register + " R" + tmpRegister1 +
               endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

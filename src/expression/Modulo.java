package expression;

import java.util.*;
import pbsc.*;

/**An expression which computes the remainder of two numbers.*/
public class Modulo extends LispExpression {

    /**
     * Create a Modulo LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing two space separated expressions to
     * compute the modulo of.
     */
    public Modulo(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {

        super(compiler, line, register, operands);

        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (MOD). Requires 2, found " +
                operands.size() + "."
            );
        }
    }

    @Override
    public String generateCode() {
        return m_operands.get(0).generateCode() +
               "COPY R" + tmpRegister0 + " R" + m_register + endl() +
               m_operands.get(1).generateCode() +
               "DIV R" + tmpRegister1 + " R" + tmpRegister0 + " R" +
               m_register + endl() +
               "MUL R" + tmpRegister1 + " R" + tmpRegister1 + " R" +
               m_register + endl() +
               "SUB R" + m_register + " R" + tmpRegister0 + " R" +
               tmpRegister1 + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

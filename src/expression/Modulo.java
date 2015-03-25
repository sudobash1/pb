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
        PbscCompiler compiler, int line, String register,
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
    public void generateCode() {
        m_operands.get(0).generateCode();
        write("PUSH", m_register);
        m_operands.get(1).generateCode();
        write("POP", tmpRegister0);
        write("DIV", tmpRegister1, tmpRegister0, m_register);
        write("MUL", tmpRegister1, tmpRegister1, m_register);
        write("SUB", m_register, tmpRegister0, tmpRegister1);
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

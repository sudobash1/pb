package expression;

import pbsc.*;

/**
 * An expression representing either a constant or a literal.
 */
public class ConstLit extends Expression {

    /**The value of this ConstLit expression.*/
    private int m_value;

    /**
     * Create a ConstLit expression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param constLitStr The string containing the constant or literal.
     */
    public ConstLit(
        PbscCompiler compiler, int line, String register, String constLitStr
    ) {
        super(compiler, line, register);

        Integer val = compiler.constLit2Integer(constLitStr, line);

        if (val != null) {
            m_value = val;
        }
    }

    @Override
    public void generateCode() {
        write("SET", m_register, ""+m_value);
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

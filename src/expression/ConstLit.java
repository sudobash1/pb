package expression;

import pbsc.*;

public class ConstLit extends Expression {

    /**The value of this ConstLit expression.*/
    private int m_value;

    public ConstLit(PbscCompiler compiler, int line, String constLitStr) {
        super(compiler, line);

        Integer val = compiler.constLit2Integer(constLitStr, line);

        if (val != null) {
            m_value = val;
        }
    }

    @Override
    public String generateCode() {
        if (m_register >= 0) {
            return "SET R" + m_register + " " + m_value + m_compiler.lineEnding();
        } else {
            return "SET R1 " + m_value + m_compiler.lineEnding() +
                   "PUSH R1 " + m_compiler.lineEnding();
        }
    }

    @Override
    public int stackReq() {
        return m_register >= 0 ? 0 : 1;
    }

    @Override
    public boolean canPlaceInRegister() { return true; }
}

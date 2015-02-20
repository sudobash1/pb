package expression;

import pbsc.*;
import command.*;

public class IntVariablePointer extends Expression {

    /**The variable definition of the variable we are pointing to.*/
    private VariableDefinition m_vd;

    public IntVariablePointer(PbscCompiler compiler, int line, String variable) {
        super(compiler, line);
        m_vd = compiler.getVarableDefinition(variable, line);
    }

    @Override
    public String generateCode() {
        if (m_register >= 0) {
            return "SET R" + m_register + " " + m_vd.getAddress() + m_compiler.lineEnding();
        } else {
            return "SET R1 " + m_vd.getAddress() + m_compiler.lineEnding() +
                   "PUSH R1 " + m_compiler.lineEnding();
        }
    }

    @Override
    public int pidgenInstructionsNeeded() {
        return m_register >= 0 ? 0 : 2;
    }

    @Override
    public int stackReq() {
        return m_register >= 0 ? 0 : 1;
    }

    @Override
    public boolean canPlaceInRegister() { return true; }
}

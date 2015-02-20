package expression;

import pbsc.*;
import command.*;

public class IntVariable extends Expression {

    /**The variable definition of the variable we are getting data from.*/
    private VariableDefinition m_vd;

    public IntVariable(PbscCompiler compiler, int line, String variable) {
        super(compiler, line);
        m_vd = compiler.getVarableDefinition(variable, line);
    }

    @Override
    public String generateCode() {
        if (m_register >= 0) {
            return "SET R" +m_register + " " + m_vd.getAddress() + m_compiler.lineEnding() +
                   "LOAD R" +m_register + " R" +m_register + m_compiler.lineEnding();
        } else {
            return "SET R1 " + m_vd.getAddress() + m_compiler.lineEnding() +
                   "LOAD R2 R1" + m_compiler.lineEnding() +
                   "PUSH R2";
        }
    }

    @Override
    public int pidgenInstructionsNeeded() {
        return 0; //XXX
    }

    @Override
    public int stackReq() {
        return m_register >= 0 ? 0 : 1;
    }

    @Override
    public boolean canPlaceInRegister() { return true; }
}

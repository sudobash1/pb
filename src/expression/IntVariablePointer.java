package expression;

import pbsc.*;
import command.*;

public class IntVariablePointer extends Expression {

    /**The variable definition of the variable we are pointing to.*/
    private VariableDefinition m_vd;

    /**
     * Create a integer variable pointer expression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param variable The name of the integer variable.
     */
    public IntVariablePointer(PbscCompiler compiler, int line, int register, String variable) {
        super(compiler, line, register);
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
    public int stackReq() {
        return 0;
    }

}

package expression;

import pbsc.*;
import command.*;

public class IntVariable extends Expression {

    /**The variable definition of the variable we are getting data from.*/
    private VariableDefinition m_vd;

    /**
     * Create a integer variable expression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param variable The name of the integer variable.
     */
    public IntVariable(PbscCompiler compiler, int line, int register, String variable) {
        super(compiler, line, register);

        m_vd = compiler.getVarableDefinition(variable, line);

        if (m_vd == null) {
            return;
        }
        if (! (m_vd instanceof IntDefinition)) {
            compiler.error(line, "`" + variable + "' is not an int.");
            return;
        }

    }

    @Override
    public String generateCode() {
        return "SET R" +m_register + " " + m_vd.getAddress() +
               m_compiler.lineEnding() +
               "LOAD R" +m_register + " R" +m_register +
               m_compiler.lineEnding();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

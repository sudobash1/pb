package expression;

import pbsc.*;
import command.*;

public class ListVariable extends Expression {

    /**The variable definition of the variable we are getting data from.*/
    private VariableDefinition m_vd;

    /**The expression to generate the index from.*/
    private Expression m_indexExp;

    /**
     * Create a list variable expression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param variable The name of the list variable.
     */
    public ListVariable(
        PbscCompiler compiler, int line, int register, String variable,
        String index
    ) {
        super(compiler, line, register);
        m_vd = compiler.getVarableDefinition(variable, line);
        m_indexExp = Expression.create(compiler, line, register, index);
    }

    @Override
    public String generateCode() {
        return ""; //XXX
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

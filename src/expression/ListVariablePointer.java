package expression;

import pbsc.*;
import command.*;

public class ListVariablePointer extends Expression {

    /**The variable definition of the variable we are pointing to.*/
    private VariableDefinition m_vd;

    /**The expression to generate the index from.*/
    private Expression m_indexExp;

    /**
     * Create a list variable pointer expression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param variable The name of the list variable.
     */
    public ListVariablePointer(
        PbscCompiler compiler, int line, int register,
        String variable, String indexExpr
    ) {
        super(compiler, line, register);

        m_vd = compiler.getVarableDefinition(variable, line);
        m_indexExp = Expression.create(compiler, line, register, indexExpr);
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

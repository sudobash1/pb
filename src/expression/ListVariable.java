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
     * @param indexExpr The expression which gives the index of the value to
     * evaluate to.
     */
    public ListVariable(
        PbscCompiler compiler, int line, int register, String variable,
        String indexExpr
    ) {
        super(compiler, line, register);

        m_vd = compiler.getVarableDefinition(variable, line, true);

        if (m_vd == null) {
            return;
        }
        if (! (m_vd instanceof ListDefinition)) {
            compiler.error(line, "`" + variable + "' is not a list.");
            return;
        }

        m_indexExp = Expression.create(
            compiler, line, register, indexExpr
        );
    }

    @Override
    public String generateCode() {
        return m_indexExp.generateCode() +
               "COPY R" + tmpRegister0  + " R" + m_register + endl() +
               "SET R" + m_register + " " + m_vd.getAddress() + endl() +
               "ADD R" + m_register + " R" +m_register + " R" + tmpRegister0 +
               endl() +
               "LOAD R" +m_register + " R" +m_register + endl();
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

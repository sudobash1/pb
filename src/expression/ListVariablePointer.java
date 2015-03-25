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
     * @param indexExpr The expression which gives the index of the value to
     * point to.
     */
    public ListVariablePointer(
        PbscCompiler compiler, int line, String register,
        String variable, String indexExpr
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
    public void generateCode() {
        m_indexExp.generateCode();
        write("COPYR", tmpRegister1, m_register);
        write("SET", m_register, ""+m_vd.getAddress());
        write("ADD", m_register, m_register, tmpRegister1);
    }

    @Override
    public int stackReq() {
        return 0;
    }

}

package expression;

import pbsc.*;
import command.*;

public class ListVariablePointer extends Expression {

    /**The variable definition of the variable we are pointing to.*/
    private VariableDefinition m_vd;

    /**The expression to generate the index from.*/
    private Expression m_indexExp;

    public ListVariablePointer(PbscCompiler compiler, int line, String variable, String indexExpr) {
        super(compiler, line);

        m_vd = compiler.getVarableDefinition(variable, line);
        m_indexExp = Expression.create(compiler, line, indexExpr);
    }

    @Override
    public String generateCode() {
        return ""; //XXX
    }

    @Override
    public int stackReq() {
        return m_register >= 0 ? 0 : 1;
    }

    @Override
    public boolean canPlaceInRegister() { return true; }
}

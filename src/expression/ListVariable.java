package expression;

import pbsc.*;
import command.*;

public class ListVariable extends Expression {

    /**The variable definition of the variable we are getting data from.*/
    private VariableDefinition m_vd;

    /**The expression to generate the index from.*/
    private Expression m_indexExp;

    public ListVariable(PbscCompiler compiler, int line, String variable, String index) {
        super(compiler, line);
        m_vd = compiler.getVarableDefinition(variable, line);
        m_indexExp = Expression.create(compiler, line, index);
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
    public boolean canPlaceInRegister() { return false; }
}

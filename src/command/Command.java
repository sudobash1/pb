package command;

import pbsc.*;

public abstract class Command {

    protected PbscCompiler m_compiler = null;

    /* The below registers must not be clobbered by any expression evaluation.
     * They may only be modified if the expression is initialized to save to
     * on of them.
     */

    /**The destination register for the LValue*/
    public final static int LRegister = 3;
    /**The destination register for the RValue*/
    public final static int RRegister = 4;


    /**Matches all valid identifiers. Prefix with a `#' to make it a constant*/
    public final static String idReStr = "[a-zA-Z_][a-zA-Z0-9_]*";

    /**Matches any valid operator to use in a lisp expression.
     * Only group is entire match.
     */
    public final static String operatorReStr = "(and\\s|or\\s|=|<|>|<=|>=|!=|\\+|-|\\*|/|^|mod\\s)";

    /**Matches any valid lisp-style expression.
     * May match lisp-style expressions with invalid operands expressions.
     * Two groups are 1) the operator and 2) the operands.
     */
    public final static String lispExpReStr = "\\(\\s*"+operatorReStr+"\\s*(.*)\\)";

    /**Matches any valid list expression.
     * May match list expressions with invalid index expressions.
     * Two groups are 1) the list name and 2) the index expression.
     */
    public final static String listVarReStr = "(" + idReStr +")\\s*\\[(.*)\\]";

    /**Matches any valid constant or integer literal.
     * Only group is entire match.
     */
    public final static String constLitReStr = "(#"+idReStr+"|[1-9][0-9]*)";

    /**Matches any valid expression.
     * May match invalid expressions as well.
     * Only group is entire match.
     */
    public final static String expressionReStr =
        "(" + lispExpReStr + "|" + listVarReStr + "|" + idReStr + "|" + constLitReStr + ")";

    protected int m_line;

    public Command(PbscCompiler compiler, int line) {
        m_compiler = compiler;
        m_line = line;
    }

    /**
     * Generate the pidgen code for this command.
     * @return Returns the pidgen code as a String.
     */
    public abstract String generateCode();

    /**
     * Determine the amount of minimum free stack space required to evaluate
     * this command.
     * @return The number of bytes minimum needed free on the stack.
     */
    public abstract int stackReq();

}

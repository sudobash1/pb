package expression;

import java.util.regex.*;
import pbsc.*;
import command.*;

public abstract class Expression extends Command {

    /**If this expression can place the result in a register, then this is
     * the register it will be placed in.
     * If it is negative, then it will be pushed to the stack.*/
    protected int m_register;

    /**An optional comment describing the purpose of this expression*/
    public String m_comment = null;
    
    public Expression(PbscCompiler compiler, int line, int register) {
        super(compiler, line);
        m_register = register;
    }

    /**Creates an Expression instance which will evaluate the passed in
     * expression. An expression factory.
     * The expression may contain nested expressions.
     * If there is an error then it will be automatically reported.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param expStrin The expression strin.
     * @return The proper expression class or null if error.
     */
    public static Expression create(
            PbscCompiler compiler, int line, int register, String expStr
    ) {

        expStr = expStr.trim();

        Matcher m;

        m = Pattern.compile("^"+constLitReStr+"$").matcher(expStr);
        if ( m.find()) {
            return new ConstLit(compiler, line, register, expStr);
        }

        m = Pattern.compile("^"+idReStr+"$").matcher(expStr);
        if ( m.find()) {
            return new IntVariable(compiler, line, register, expStr);
        }

        m = Pattern.compile("^"+listVarReStr+"$").matcher(expStr);
        if ( m.find()) {
            return new ListVariable(
                compiler, line, register, m.group(1), m.group(2)
            );
        }

        m = Pattern.compile("^"+lispExpReStr+"$").matcher(expStr);
        if ( m.find()) {
            return LispExpression.create(
                compiler, line, register, m.group(1), m.group(2)
            );
        }

        compiler.error(line ,"Malformed expression `" + expStr + "'.");
        return null;
    }

    /**Creates an Expression instance which will evaluate the passed in
     * expression. An expression factory.
     * The expression may contain nested expressions.
     * If there is an error then it will be automatically reported.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param expStrin The expression strin.
     * @param comment An optional comment for the Expression.
     * @return The proper expression class or null if error.
     */
    public static Expression create(
            PbscCompiler compiler, int line, int register, String expStr,
            String comment
    ) {
        Expression expr = create(compiler, line, register, expStr);
        if (expr != null) {
            expr.m_comment = comment;
        }
        return expr;
    }

}

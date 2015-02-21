package expression;

import java.util.regex.*;
import pbsc.*;
import command.*;

public abstract class Expression extends Command {

    /**If this expression can place the result in a register, then this is
     * the register it will be placed in.
     * If it is negative, then it will be pushed to the stack.*/
    protected int m_register = -1;
    
    public Expression(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    /**
     * Select the register to place the value in.
     * If it is negative then the stack is used.
     * Only useful if canPlaceInRegister returns true.
     * @param register The register to place the result in.
     */
    public final void setRegister(int register) {
        m_register = register;
    }

    /**
     * Returns if this expression can place the result in a register without
     * clobbering any other registers.
     * @return Can a the result be placed in a register without clobbering.
     */
    public abstract boolean canPlaceInRegister();

    /**Creates an Expression instance which will evaluate the passed in
     * expression.
     * The expression may contain nested expressions.
     * If there is an error then it will be automatically reported.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param expStrin The expression strin.
     * @return The proper expression class or null if error.
     */
    public static Expression create(PbscCompiler compiler, int line, String expStr) {

        expStr = expStr.trim();

        Matcher m;

        m = Pattern.compile("^"+constLitReStr+"$").matcher(expStr);
        if ( m.find()) {
            return new ConstLit(compiler, line, expStr);
        }

        m = Pattern.compile("^"+idReStr+"$").matcher(expStr);
        if ( m.find()) {
            return new IntVariable(compiler, line, expStr);
        }

        m = Pattern.compile("^"+listVarReStr+"$").matcher(expStr);
        if ( m.find()) {
            return new ListVariable(compiler, line, m.group(1), m.group(2));
        }

        m = Pattern.compile("^"+lispExpReStr+"$").matcher(expStr);
        if ( m.find()) {
            /*switch (m.group(1)) {
            }*/
        }

        compiler.error(line ,"Malformed expression.");
        return null;
    }

}

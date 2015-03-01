package expression;

import java.util.*;
import java.util.regex.*;
import pbsc.*;

public abstract class LispExpression extends Expression {

    /**A list of the operands this lisp expression operates on.*/
    protected ArrayList<Expression> m_operands = null;

    /**
     * A simple tuple class which holds an Expression and a String.
     * @see expressionPop
     */
    private static class ExpressionStringTuple { 
        public final Expression expression; 
        public final String string; 
        public ExpressionStringTuple(Expression e, String s) { 
            expression = e;
            string = s;
        } 
    } 

    /**
     * Create a LispExpression instance.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param operands A string containing whitespace separated expressions
     * which the LispExpression will operate on.
     */
    public LispExpression(
        PbscCompiler compiler, int line, int register,
        ArrayList<Expression> operands
    ) {
        super(compiler, line, register);
        m_operands = operands;
    }

    /**
     * Takes a string and returns the first expression found on it and the
     * remaining string.
     * These expressions in the string must be separated by a whitespace.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param operands The string containing the operands.
     * @return An ExpressionStringTuple containing the new expression and what
     * is left of the expression string. Returns null on error or empty string.
     */
    private static ExpressionStringTuple expressionPop(
        PbscCompiler compiler, int line, int register, String operands
    ) {

        operands = operands.trim();

        if (operands.length() == 0) {
            return null;
        }

        ExpressionStringTuple ret;
        Matcher m;

        //Test if the first expression is a lisp expression.
        if (operands.charAt(0) == '(') {

            //We must itterate through the operands chars to find where the end
            //of this expression is because we may have nested lisp expressions.

            int nestLevel = 0;
            boolean buildOperand = true;
            StringBuilder operandBuilder = new StringBuilder();
            StringBuilder remainingBuilder = new StringBuilder();
            for (char c : operands.toCharArray()) {

                if (buildOperand) {
                    operandBuilder.append(c);

                    if (c == '(') {
                        nestLevel += 1;
                    }
                    if (c == ')') {
                        nestLevel -= 1;
                        if (nestLevel == 0) {
                            buildOperand = false;
                        }
                    }
                } else {
                    remainingBuilder.append(c);
                }
            }

            if (nestLevel != 0) {
                //We never found the last closing paren. Bad expression.
                compiler.error(line, "Malformed expression. Missing closing `)'.");
                return null;
            }

            //Extract the opperator and opperands from this lisp expression to
            //create a new expression.
            m = Pattern.compile(lispExpReStr).matcher(operandBuilder);
            m.find();

            Expression newExp = create(
                compiler, line, register, m.group(1), m.group(2)
            );
            ret = new ExpressionStringTuple(newExp, remainingBuilder.toString());

        } else if (operands.matches("^" + listVarReStr)) {
            //This is a list expression, and may be nested. Time to extract...
            
            //We must itterate through the operands chars to find where the end
            //of this expression is because we may have nested list expressions.

            int nestLevel = 0;
            boolean buildOperand = true;
            StringBuilder operandBuilder = new StringBuilder();
            StringBuilder remainingBuilder = new StringBuilder();
            for (char c : operands.toCharArray()) {

                if (buildOperand) {
                    operandBuilder.append(c);

                    if (c == '[') {
                        nestLevel += 1;
                    }
                    if (c == ']') {
                        nestLevel -= 1;
                        if (nestLevel == 0) {
                            buildOperand = false;
                        }
                    }
                } else {
                    remainingBuilder.append(c);
                }
            }

            if (nestLevel != 0) {
                //We never found the last closing paren. Bad expression.
                compiler.error(line, "Malformed expression. Missing closing `]'.");
                return null;
            }

            Expression newExp = Expression.create(
                compiler, line, register, operandBuilder.toString()
            );
            ret = new ExpressionStringTuple(newExp, remainingBuilder.toString());
        } else {

            //This a simple, non-nestable expression, so we may use a regex
            //to extract the data

            String reStr = "^" + expressionReStr + "(\\s*" + expressionReStr + ")*$";
            m = Pattern.compile(reStr).matcher(operands);

            if (!m.find()) {
                //Check if there was an extra `)'
                if (operands.charAt(0) == ')') {
                    compiler.error(line, "Malformed expression. Extra `)'.");
                } else {
                    compiler.error(line, "Malformed expression.");
                }
                return null;
            }

            Expression newExp = Expression.create(
                compiler, line, register, m.group(1)
            );
            String remaining = operands.substring(m.group(1).length());

            ret = new ExpressionStringTuple(newExp, remaining);
        }

        return ret;
    }


    /**
     * A LispExpression factory.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     * @param register The register to save the value to when the expression
     * is evaluated at runtime.
     * @param op The operator for the LispExpression.
     * @param operands A string containing whitespace separated expressions
     * which the LispExpression will operate on.
     */
    public static LispExpression create(
        PbscCompiler compiler, int line, int register, String op,
        String operands
    ) {

        //Compile the operands string into a list of Expressions.
        ArrayList<Expression> exprs = new ArrayList<Expression>();

        ExpressionStringTuple pop;
        pop = expressionPop(compiler, line, register, operands);
        while (pop != null) {
            exprs.add(pop.expression);
            operands = pop.string;
            pop = expressionPop(compiler, line, register, operands);
        } 

        switch (op.trim()) {
            case "+": return new Plus(compiler, line, register, exprs);
            case "!=": return new NotEquals(compiler, line, register, exprs);
        }

        compiler.error(
            line, "Invalid operator for lisp expression `" + op.trim() + "'"
        );

        return null;
    }

}

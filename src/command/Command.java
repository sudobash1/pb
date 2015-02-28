package command;

import java.util.regex.*;
import pbsc.*;

/**
 * The base class for all sections of the program.
 * Each command class is able to create its own pidgen code. Commands may also
 * contain other commands (expressions) which they subcall to create their
 * code.
 * @see Expression
 */
public abstract class Command {

    /**The main instance of the PbscCompiler.*/
    protected PbscCompiler m_compiler = null;

    /**
     * The line the command was found on.
     * (This is the line the ; is on.)
     */
    protected int m_line;

    /**The string the command was created from*/
    private String m_commandString = "";

    /* The below registers must not be clobbered by any expression evaluation.
     * They may only be modified if the expression is initialized to save to
     * on of them.
     */

    /**The destination register for the LValue*/
    public final static int LRegister = 3;
    /**The destination register for the RValue*/
    public final static int RRegister = 4;

    /**A temp register. Clobber at will.*/
    public final static int tmpRegister1 = 1;
    /**A temp register. Clobber at will.*/
    public final static int tmpRegister2 = 2;

    /**Extract the command name and arguments from a string*/
    private final static String m_commandArgsReStr = "^([a-zA-Z]+)\\s(.*)$";

    /**Matches all valid identifiers. Prefix with a `#' to make it a constant*/
    public final static String idReStr = "[a-zA-Z_][a-zA-Z0-9_]*";

    /**Matches any valid operator to use in a lisp expression.
     * Only group is entire match.
     */
    public final static String operatorReStr =
        "(and\\s|or\\s|=|<|>|<=|>=|!=|\\+|-|\\*|/|^|mod\\s)";

    /**Matches any valid lisp-style expression.
     * May match lisp-style expressions with invalid operands expressions.
     * Two groups are 1) the operator and 2) the operands.
     */
    public final static String lispExpReStr =
        "\\(\\s*"+operatorReStr+"\\s*(.*)\\)";

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
        "(" + lispExpReStr + "|" + listVarReStr + "|" + idReStr + "|" +
        constLitReStr + ")";

    /**
     * Create a command.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the expression was found on.
     */
    public Command(PbscCompiler compiler, int line) {
        m_compiler = compiler;
        m_line = line;
    }

    /**
     * Generate the pidgen code for this command.
     * @return Returns the pidgen code as a String.
     */
    public String generateCode() {
        if (m_compiler.debugging() && !m_commandString.equals("")) {
            return "# " + m_commandString + m_compiler.lineEnding();
        }
        return "";
    }

    /**
     * Determine the amount of minimum free stack space required to evaluate
     * this command.
     * @return The number of bytes minimum needed free on the stack.
     */
    public abstract int stackReq();

    /**
     * Creates a command from a given string. A command factory.
     * The `;' must be removed from end of the string.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on (line of the `;' char)
     * @param s The string containing the command.
     * @return The generated command, or null on error.
     */
    public static Command create(PbscCompiler compiler, int line, String s) {

        //Make lower case, remove newlines and tabs, and trim
        s = s.toLowerCase().replaceAll("\\s" , " ").trim();

        //Check if this line is empty
        if (s.equals("")) { return null; }

        Matcher commandMatcher = Pattern.compile(m_commandArgsReStr).matcher(s);
        Matcher argsMatcher = Pattern.compile(m_commandArgsReStr).matcher(s);

        if (! commandMatcher.find() || ! argsMatcher.find()) {
            compiler.error(line, "Malformed line.");
            return null;
        }

        String commandName = commandMatcher.group(1).toLowerCase();
        String commandArgs = argsMatcher.group(2);

        Command cmd = null;

        switch (commandName) {
            case "define":
                cmd = new Define(compiler, line, commandArgs);
                break;
            case "int":
                cmd = new IntDefinition(compiler, line, commandArgs);
                break;
            case "list":
                cmd = new ListDefinition(compiler, line, commandArgs);
                break;
            case "rem":
                cmd = new Rem(compiler, line);
                break;
            case "set":
                cmd = new Set(compiler, line, commandArgs);
                break;
            default: 
                compiler.error(line, "Invalid command `" + commandName + "'");
                return null;
        }

        cmd.m_commandString = s;

        return cmd;
    }


}

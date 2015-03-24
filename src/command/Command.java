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
    protected final int m_line;

    /**The string the command was created from*/
    private String m_commandString = "";

    /**If we are currently within a subroutine, then this is a reference
     * to the Sub instance. Null otherwise.
     */
    protected static Sub currentSub = null;

    /* The below registers must not be clobbered by any expression evaluation.
     * They may only be modified if the expression is initialized to save to
     * on of them.
     */

    /**The destination register for the LValue*/
    public final static int LRegister = 3;
    /**The destination register for the RValue*/
    public final static int RRegister = 4;
    /**The destination where the value of an if statement gets evaluated.*/
    public final static int ifRegister = 4;
    /**The destination where the value of an while statement gets evaluated.*/
    public final static int whileRegister = 4;
    /**Where expressions which are arguments for the Trapper get placed.*/
    public final static int trapperRegister = 4;

    /* The below are tmp registers. Expressions will not evaluate to them.*/
    /**A temp register. Clobber at will.*/
    public final static int tmpRegister0 = 0;
    /**A temp register. Clobber at will.*/
    public final static int tmpRegister1 = 1;
    /**A temp register. Clobber at will.*/
    public final static int tmpRegister2 = 2;

    /**Extract the command name and arguments from a string*/
    private final static String m_commandArgsReStr = "^([a-zA-Z]+)\\s*(.*)$";

    /**Matches all valid identifiers. Prefix with a `#' to make it a constant*/
    public final static String idReStr = "[a-zA-Z_][a-zA-Z0-9_]*";

    /**Matches any valid operator to use in a lisp expression.
     * Only group is entire match.
     */
    public final static String operatorReStr =
        "(and\\s|or\\s|not\\s|<=?|>=?|!?=|\\+|-|\\*|/|^|mod\\s)";

    /**Matches any valid lisp-style expression.
     * May match lisp-style expressions with invalid operands expressions.
     * Two groups are 1) the operator and 2) the operands.
     */
    public final static String lispExpReStr =
        "\\(\\s*"+operatorReStr+"\\s*([^=].*)\\)";
    // There is a [^=] in the above regex so that <= cannot be matched to <
    // with malformed arguments

    /**Matches any valid list expression.
     * May match list expressions with invalid index expressions.
     * Two groups are 1) the list name and 2) the index expression.
     */
    public final static String listVarReStr = "(" + idReStr +")\\s*\\[(.*)\\]";

    /**Matches any valid constant or integer literal.
     * Only group is entire match.
     */
    public final static String constLitReStr =
        "(#"+idReStr+"|-?[1-9][0-9]*|0)";

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
     * Return the line ending.
     */
    protected final String endl() {
        return m_compiler.lineEnding();
    }

    /**
     * Generate the pidgen code for this command.
     * @return Returns the pidgen code as a String.
     */
    public String generateCode() {
        if (m_compiler.debugging() && !m_commandString.equals("")) {
            return "#> " + m_commandString + endl();
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
     * Check that all required labels exist.
     */
    public void checkLabels() {}

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

        Matcher m = Pattern.compile(m_commandArgsReStr).matcher(s);

        if (! m.find()) {
            compiler.error(line, "Malformed line.");
            return null;
        }

        String commandName = m.group(1).toLowerCase();
        String commandArgs = m.group(2);

        Command cmd = null;

        boolean usedArgs = false;

        switch (commandName) {
            case "break":
                cmd = new Break(compiler, line);
                break;
            case "bus":
                cmd = new Bus(compiler, line);
                break;
            case "close":
                cmd = new Close(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "define":
                cmd = new Define(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "done":
                cmd = new Done(compiler, line);
                break;
            case "dump":
                cmd = new Dump(compiler, line);
                break;
            case "else":
                cmd = new Else(compiler, line);
                break;
            case "exec":
                cmd = new Exec(compiler, line);
                break;
            case "exit":
                cmd = new Exit(compiler, line);
                break;
            case "getpid":
                cmd = new GetPid(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "fi":
                cmd = new Fi(compiler, line);
                break;
            case "for":
                cmd = new For(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "goto":
                cmd = new Goto(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "gosub":
                cmd = new Gosub(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "if":
                cmd = new If(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "input":
                cmd = new Input(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "int":
                cmd = new IntDefinition(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "label":
                cmd = new Label(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "list":
                cmd = new ListDefinition(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "open":
                cmd = new Open(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "p":
                cmd = new P(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "print":
                cmd = new Print(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "sub":
                cmd = new Sub(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "read":
                cmd = new Read(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "return":
                cmd = new Return(compiler, line);
                break;
            case "rem":
                cmd = new Rem(compiler, line);
                usedArgs = true; //Rem ignores its args
                break;
            case "set":
                cmd = new Set(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "setl":
                cmd = new Setl(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "while":
                cmd = new While(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "write":
                cmd = new Write(compiler, line, commandArgs);
                usedArgs = true;
                break;
            case "yield":
                cmd = new Yield(compiler, line);
                break;
            default: 
                compiler.error(line, "Invalid command `" + commandName + "'");
                return null;
        }

        if (!usedArgs && !commandArgs.trim().equals("")) {
            compiler.error(line, "Expected ; after `" + commandName + "'");
        }

        cmd.m_commandString = s;

        return cmd;
    }


}

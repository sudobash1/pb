package pbsc;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.regex.*;
import command.*;

public class PbscCompiler {

    /**Turns on debugging output*/
    private final boolean m_debug = true;

    /**Maximum number of errors to report before aborting the compilation.*/
    private final int MAX_ERROR = 5;
    /**Number of errors found so far.*/
    private int m_errorCount = 0;
    
    /**Set to true when there has been an error reported.*/
    private boolean m_hasError = false;

    /**
     * A number not found anywhere in the program input.
     * It is used to make labels for compiler use that will not interfere with
     * user made labels.
     */
    private int m_magicNumber;

    /**
     * Maps constants defined in the program to their values.
     * The String is the constant name with scope mangling.
     */
    private Hashtable<String, Integer> m_definesTable = null;

    /**
     * Maps integers to their definition class.
     * The String is the variable name with scope mangling.
     */
    private Hashtable<String, VariableDefinition> m_varTable = null;

    /**
     * Every time another scope is entered, the block id gets pushed here.
     * The String is the If/While/For/Sub block id.
     */
    private ArrayList<String> m_namespaceStack = null;

    public PbscCompiler() {
        m_varTable = new Hashtable<String, VariableDefinition>();
        m_definesTable = new Hashtable<String,Integer>();
        m_namespaceStack = new ArrayList<String>();
    }

    /**
     * Returns if debugging is on.
     * @return true if debugging is enabled.
     */
    public boolean debugging() {
        return m_debug;
    }

    /**
     * Prints an error message to the screen and flags the compilation
     * as failed.
     * @param line Line where the error occurred.
     * @param message Error message to display.
     */
    public void error(int line, String message) {
        System.err.print("ERROR: line " + line + ": ");
        System.err.println(message);
        m_hasError = true;
        if (++m_errorCount >= MAX_ERROR) {
            System.err.println(
                "Aborting compilation attempt. No more errors will be reported."
            );
            System.err.println("Compilation failed.");
            System.exit(2);
        }
    }

    /**
     * Prints a warning message to the screen but does not flag the compilation
     * as failed.
     * @param line Line where the warning was generated.
     * @param message Warning message to display.
     */
    public void warning(int line, String message) {
        System.err.print("Warning: line " + line + ": ");
        System.err.println(message);
    }

    /**
     * Returns the line ending used for the generated pidgen file. For now
     * it is just hardcoded to "\n".
     * @return the line ending string.
     */
    public String lineEnding() { return "\n"; };

    /**
     * Register a new constant. Checks if constant exists before adding.
     * If the constant already exists, prints out an error.
     * This method is scope aware.
     * @param constName the constant name (without scope mangling).
     * @param value the value of the constant.
     * @param int the line the const is being declaired.
     * @return False if constant already exists.
     */
    public boolean registerNewConstant(String constName, Integer value, int line) {
        //Check if constant exists in this scope.
        for (String id : allId(constName)) {
            if (m_definesTable.containsKey(id)) {
                error(line, "Constant `" + constName + "' already exists.");
                return false;
            }
        }
        m_definesTable.put(id(constName), value);
        return true;
    }

    /**
     * Get the value of a constant.
     * If the constant doesn't exist in this scope, prints out an error.
     * This method is scope aware.
     * @param constName the name of the constant
     * @param int the line the const is being referenced from.
     * @return the Integer value of the constant if it exists, else null.
     */
    public Integer getConstantValue(String constName, int line) {
        //Check if constant exists in this scope.
        for (String id : allId(constName)) {
            if (m_definesTable.containsKey(id)) {
                return m_definesTable.get(id);
            }
        }
        error(line, "Undefined constant `" + constName + "'");
        return null;
    }

    /**
     * Register a new varable. Checks if variable exists before adding.
     * If the variables already exists, prints out an error.
     * This method is scope aware.
     * @param varName the variable name (without scope mangling).
     * @param varDefn the VariableDefinition
     * @param int the line the variable is being declaired.
     * @return False if variable already exists.
     */
    public boolean registerNewVariable(String varName, VariableDefinition varDefn, int line) {
        //Check if variable exists in this scope.
        for (String id : allId(varName)) {
            if (m_varTable.containsKey(id)) {
                error(line, "Variable `" + varName + "' has already been declared.");
                return false;
            }
        }
        m_varTable.put(id(varName), varDefn);
        return true;
    }

    /**
     * Gets the definition class of a variable.
     * If the variable doesn't exist in this scope, prints out an error.
     * This method is scope aware.
     * @param varName the name of the variable
     * @param int the line the variable is being referenced from.
     * @return the VariableDefinition if it exists else null.
     */
    public VariableDefinition getVarableDefinition(String varName, int line) {
        //Check if variable exists in this scope.
        for (String id : allId(varName)) {
            if (m_varTable.containsKey(id)) {
                return m_varTable.get(id);
            }
        }
        error(line, "Undefined varable `" + varName + "'");
        return null;
    }

    /**
     * Returns the value of the constant or integer literal passed.
     * Handles all error chcking.
     * This method is scope aware.
     * @param constLit the string representation of the constant or literal.
     * @param int the line the constLit is being referenced from.
     * @return the Integer value the constLit represents or null on error.
     */
    public Integer constLit2Integer(String constLit, int line) {
        constLit = constLit.trim().toLowerCase();
        if (constLit.equals("")) {
            error(line, "Integer constant or literal expected");
            return null;
        }

        if (constLit.charAt(0) == '#') {
            //This is a constant
            return getConstantValue(constLit, line);
        } else {
            if (constLit.matches("[1-9][0-9]*")) {
                try {
                    return Integer.valueOf(constLit);
                } catch (NumberFormatException e) {
                    error(line, "Integer literal out of range, `" + constLit + "'.");
                    return null;
                }
            } else {
                error(line, "Integer constant or literal expected. Found `'" + constLit + "'");
                return null;
            }
        }
    }

    /**
     * Returns the passed in label with scope mangling applied.
     * @return The new if block id.
     */
    public String id(String label) {
        String namespace = "";
        for (String s : m_namespaceStack) {
            namespace += s;
        }
        return namespace + label + "ID" + m_magicNumber;
    }

    /**
     * Returns a list of all namespace magled ids which the passed in label
     * could represent. Used for determining what to link a label to.
     * @return The ArrayList of all possible ids.
     */
    private ArrayList<String> allId(String label) {
        ArrayList<String> ret = new ArrayList<String>();

        ret.add(label + "ID" + m_magicNumber);

        String namespace = "";
        for (String s : m_namespaceStack) {
            namespace += s;
            ret.add( namespace + label + "ID" + m_magicNumber );
        }

        return ret;
    }

    /**
     * Pushes a id to the scope stack. The id is the id of the if/while/for/sub
     * block just entered.
     * @param scope The scope id.
     */
    public void pushScope(String scope) {
        m_namespaceStack.add(scope);
    }

    /**
     * Pops a id from the scope stack.
     */
    public void popScope(String scope) {
        m_namespaceStack.remove(m_namespaceStack.size() - 1);
    }

    /**
     * Find how many newlines there are in the passed string. Used to advance
     * the line counter when going through the file statment by statement.
     * @return The number of newlines counted.
     */
    public static int countNewlines(String s) {
        Matcher m = Pattern.compile("(\n)").matcher(s);
        int lines = 0;
        while (m.find()) { ++lines; }
        return lines;
    }

    /**
     * Reads the file and returns the contents. If there are errors reading the
     * file, then they are printed and System.exit(1) is called.
     *
     * Note: for now '\n' are always used to denote the end of a line.
     *
     * @param path The path to the file to read.
     * @return The contents of the file with \n endings.
     */
    public String readFile(String path) {

        File file = new File(path);
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        String input;

        try {
            reader = new BufferedReader(new FileReader(file));

            while ((input = reader.readLine()) != null) {
                sb.append(input + '\n');
            }
        } catch (FileNotFoundException e) {
            System.err.println("Cannot find file `" + path + "'.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Cannot open file `" + path + "' for reading.");
            System.exit(1);
        } finally {
            try {
                if (reader != null) reader.close();
            } catch (IOException e) {}
        }

        return sb.toString();
    }

    /**
     * Writes the given string to a file.
     *
     * @param path The path to the file to written to.
     * @param content The content to be placed in the file.
     */
    public void writeFile(String path, String content) {

        PrintWriter fout = null;

        try {
            fout = new PrintWriter(path);
            fout.print(content);
        } catch (FileNotFoundException e) {
            System.err.println("Cannot open file `" + path + "' for writing.");
            System.exit(1);
        } finally {
            if (fout != null) fout.close();
        }
    }

    /**
     * Generates a string containing the addresses variables bound to as
     * Pidgen comments. Used for debugging.
     * @return A pidgen comment
     */
    private String generateSymTabComment() {

        if (! m_debug) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        sb.append("#VARIABLE TABLE##########################################");
        sb.append(lineEnding());
        sb.append("#");
        sb.append(lineEnding());
        if (m_debug) {
            for (String key : m_varTable.keySet()) {
                sb.append("# ");
                sb.append(key);
                sb.append(" @ ");
                sb.append(""+m_varTable.get(key).getAddress());
                sb.append(lineEnding());
            }
        }
        sb.append("#");
        sb.append(lineEnding());
        sb.append("#########################################################");
        sb.append(lineEnding());
        sb.append(lineEnding());

        return sb.toString();
    }

    /**
     * Run the compiler with passed in args.
     *
     * The exit codes are:
     *
     * 0 - success
     * 1 - IO error
     * 2 - compile error
     *
     * @return the exit code.
     */
    public int run(String[] args) {
        String input;

        if (args.length < 2) {
            System.err.println("Requires more arguments.");
            System.exit(1);
        }

        input = readFile(args[0]);

        //Generate a magic number not found anywhere in the input.
        do {
            m_magicNumber = (int)(Math.random() * 100000);
        } while ( input.contains(String.valueOf(m_magicNumber)) );

        int line = 1; //What line number are we on?

        //Statements are always delimited by ;
        ArrayList<String> str_commands = new ArrayList<String>(
            Arrays.asList(input.split(";"))
        );
        ArrayList<Command> program = new ArrayList<Command>();

        //Go through the list of statements found in the file and generate
        //the command for each one.
        for(String str_command: str_commands) {
            line += countNewlines(str_command);

            Command newCommand = Command.create(this, line, str_command);

            if (newCommand != null) {
                program.add(newCommand);
            }
        }

        if (m_hasError) {
            System.err.println("Compilation failed.");
            System.exit(2);
        }

        /*
         * Front end complete. Now we compile the program to pidgen asm without
         * bound variables. We need to figure out what the size of the program
         * will be before we can bind variables to addresses.
         *
         * This is a bit of a hack and makes it so we must compile like this
         * twice, but it will work for now. It is quite fast enough because
         * compilation is simple.
         */
        StringBuilder pidgenStringBuilder = new StringBuilder();
        for (Command c : program) {
            pidgenStringBuilder.append(c.generateCode());
        }

        String pidgenString = pidgenStringBuilder.toString();

        //The size taken up by program instructions. Assuming one a line.
        final int programInstSize = countNewlines(pidgenString);

        //Bind the variables to addresses.
        
        /* XXX For now we are assuming that the variables go immediately after
         * the program statements at the top of memory.
         */
        
        int variableLocation = programInstSize + 1;
        for (VariableDefinition vd : m_varTable.values()) {
            vd.setAddress(variableLocation);
            variableLocation += vd.getSize();
        }

        //The last variable is where our staticly allocated memory ends.
        final int endOfStaticMem = variableLocation;

        //Recompile with bound variables
        pidgenStringBuilder = new StringBuilder();
        for (Command c : program) {
            pidgenStringBuilder.append(c.generateCode());
        }

        pidgenString = pidgenStringBuilder.toString();

        //Generate the symbole table comment string.
        String symTabComment = generateSymTabComment();

        //Save the output to the output file.
        writeFile(args[1], symTabComment + pidgenString);

        System.out.println("Compilation successful");

        return 0;
    }

    public static void main(String[] args) {
        new PbscCompiler().run(args);
    }

}

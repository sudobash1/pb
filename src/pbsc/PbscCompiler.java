package pbsc;

import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.regex.*;
import command.*;

public class PbscCompiler {

    private boolean m_hasError = false;

    //This magicNumber is not found anywhere in the program input.
    //It is used to make labels for compiler use that will not interfere.
    private int m_magicNumber;

    private int m_ifCounter = 0;
    private int m_whileCounter = 0;
    private int m_forCounter = 0;

    //Maps constants defined in the program to their values.
    //The String is the constant name with scope mangling.
    private Hashtable<String, Integer> m_definesTable = null;

    //The String is the If/While/For/Sub block id.
    private ArrayList<String> m_namespaceStack = null;

    public PbscCompiler() {
        m_definesTable = new Hashtable<String,Integer>();
        m_namespaceStack = new ArrayList<String>();
    }

    /**
     * Prints an error message to the screen and flags the compilation
     * as failed.
     * @param line Line where the error occured.
     * @param message Error message to display.
     */
    public void error(int line, String message) {
        System.out.print("ERROR: line " + line + ": ");
        System.out.println(message);
        m_hasError = true;
    }

    /**
     * Prints a warning message to the screen but does not flag the compilation
     * as failed.
     * @param line Line where the warning was generated.
     * @param message Warning message to display.
     */
    public void warning(int line, String message) {
        System.out.print("Warning: line " + line + ": ");
        System.out.println(message);
    }

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
     * Generates a new, unique if block id.
     * @return The new if block id.
     */
    public String ifID() {
        return "IF" + (m_ifCounter++) + "ID" + m_magicNumber;
    }

    /**
     * Generates a new, unique while block id.
     * @return The new if block id.
     */
    public String whileID() {
        return "WHILE" + (m_whileCounter++) + "ID" + m_magicNumber;
    }

    /**
     * Generates a new, unique for block id.
     * @return The new if block id.
     */
    public String forID() {
        return "WHILE" + (m_forCounter++) + "ID" + m_magicNumber;
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

        if (args.length < 1) {
            System.err.println("Requires more arguments.");
            return 1;
        }

        input = readFile(args[0]);

        //Generate a magic number not found anywhere in the input.
        do {
            m_magicNumber = (int)(Math.random() * 100000);
        } while ( input.contains(String.valueOf(m_magicNumber)) );

        int line = 1; //What line number are we on?

        CommandFactory cmdFactory = new CommandFactory(this);

        //Statements are always delimited by ;
        ArrayList<String> str_commands = new ArrayList<String>(Arrays.asList(input.split(";")));
        ArrayList<Command> program = new ArrayList<Command>();

        //Go through the list of statements found in the file and generate
        //the command for each one.
        for(String str_command: str_commands) {
            line += countNewlines(str_command);

            Command newCommand = cmdFactory.GenerateCommand(str_command, line);

            if (newCommand != null) {
                program.add(newCommand);
            }
        }

        if (m_hasError) {
            return 2;
        }

        return 0;
    }

    public static void main(String[] args) {
        System.exit(new PbscCompiler().run(args));
    }

}

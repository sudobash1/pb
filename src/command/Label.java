package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Creates a user label.
 */
public class Label extends Command {

    private final static ArrayList<String> labels = new ArrayList<String>();

    /**The label name.*/
    public final String m_name;

    /**The scope the label was found in.*/
    public final ArrayList<String> m_namespaceStack;

    /**
     * Creates a user label.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param name The label name. 
     */
    public Label(PbscCompiler compiler, int line, String name) {
        super(compiler, line);
        m_name = name.trim();

        if (compiler.isReservedWord(m_name)) {
            compiler.error(line, "Illegal name for label `" + m_name + "'.");
            m_namespaceStack = null;
            return;
        }

        if (labels.contains(m_name)){
            compiler.error(line, "Label `" + m_name + "' alread exists.");
            m_namespaceStack = null;
            return;
        }
        labels.add(m_name);
        m_namespaceStack = compiler.copyNamespace();
    }

    /**
     * Returns if label was defined. Prints an error if it doesn't exits.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the label was referenced from.
     * @param label The label to test for existance.
     * @return true iff the label exists. 
     */
    public static boolean checkExists(
        PbscCompiler compiler, int line, String label
    ) {
        if (! labels.contains(label)){
            compiler.error(line, "Label `" + label + "' doesn't exists.");
            return false;
        }
        return true;
    }

    @Override
    public String generateCode() {
        return super.generateCode() + ":" + m_name + m_compiler.lineEnding();
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

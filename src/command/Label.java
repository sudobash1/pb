package command;

import java.util.*;
import pbsc.*;
import expression.*;

/**
 * Creates a user label.
 */
public class Label extends Command {

    /**Global labels.*/
    private final static Hashtable<String, Label> globalLabels =
        new Hashtable<String, Label>();

    /**Labels local to a sub.*/
    private final static Hashtable<Sub, Hashtable<String, Label>> subLabels =
        new Hashtable<Sub, Hashtable<String, Label>>();

    /**The label name.*/
    public String m_name;

    /**The subroutine this lable was found in. May be null.*/
    private Sub m_withinSub;

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
            return;
        }

        m_withinSub = currentSub;

        if (currentSub != null) {

            if (subLabels.containsKey(currentSub)) {
                //Test if Label is already in local symbol table.
                if (subLabels.get(currentSub).containsKey(m_name)) {
                    compiler.error(
                        line, "Label `" + m_name + "' already exists " +
                        "in subroutine `" + currentSub.name + "'."
                    );
                    return;
                }
            }

            //Insert the label into the local label symbol table.
            subLabels.get(currentSub).put(m_name, this);

        } else {

            //Check if this label exists globally
            if (globalLabels.containsKey(m_name)){
                compiler.error(line, "Label `" + m_name + "' already exists.");
                return;
            }
            //Insert this label into the local label symbol table.
            globalLabels.put(m_name, this);
        }
    }

    /**
     * Returns a Label.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the label was referenced from.
     * @param label The label name to test for existance.
     * @param sub The subroutine label was referenced from. May be null.
     * @return the Label if there is one else null
     */
    public static Label retriveLabel(
        PbscCompiler compiler, int line, String label, Sub sub
    ) {
        if (sub == null || ! subLabels.get(sub).containsKey(label)) {
            if (! globalLabels.containsKey(label)){
                compiler.error(line, "Label `" + label + "' doesn't exist.");
                return null;
            }
            return globalLabels.get(label);
        }

        if (! subLabels.get(sub).containsKey(label)) {
            compiler.error(line, "Label `" + label + "' doesn't exist.");
            return null;
        }

        return subLabels.get(sub).get(label);
    }

    /**
     * Creates a local label table for a sub.
     * @param sub The subroutine to create a local label table for.
     */
    protected static void createLocalLabelTable(Sub sub) {
        //Create a local label symbol table
        subLabels.put(sub, new Hashtable<String, Label>());
    }

    /**
     * Returns if this label is in a subroutine.
     * @return if this label is in a subroutine.
     */
    protected boolean inSub() {
        return !(m_withinSub == null);
    }

    /**
     * Returns the text of this Label.
     * @return the text of this Label.
     */
    protected String text() {
        if (m_withinSub == null) {
            return m_name;
        } else {
            return m_withinSub.name + m_name;
        }
    }

    @Override
    public void generateCode() {
        super.generateCode();
        write(":" + text());
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }
}

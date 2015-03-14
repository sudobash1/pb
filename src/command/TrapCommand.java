package command;

import java.util.regex.*;
import java.util.*;
import pbsc.*;
import expression.*;

/**
 * A command to execute a trap command and examine its success code.
 */
public abstract class TrapCommand extends Command {

    /**Maps error integers to labels to jump to on error.*/
    protected final Hashtable<Integer, String> m_errorMap;

    /**The default label to jump to on uncaught non-success code.*/
    protected final String m_defaultLabel;

    /**The regular expression to extract the 
     * ON (error const/lit) (error label)
     * statements from the arguments.
     */
    protected final String m_onReStr = 
        "\\s+on\\s*" + constLitReStr + "\\s+goto\\s+(" + idReStr + ")";

    /**The regular expression to extract the 
     * DEFAULT (error label)
     * statement from the arguments.
     */
    protected final String m_defaultReStr = "\\s+default\\s+goto\\s+(" + idReStr + ")";

    /**
     * Create a new TrapCommand instance. 
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     * @param arguments The string which contains multiple ON statements.
     */
    public TrapCommand(PbscCompiler compiler, int line, String arguments) {
        super(compiler, line);
        m_errorMap = new Hashtable<Integer, String>();;

        //Extract the on statements.
        Matcher m = Pattern.compile(m_onReStr).matcher(arguments);
        while (m.find()) {
            Integer errorNo = m_compiler.constLit2Integer(m.group(1), line);
            if (errorNo == null) { continue; /*check for more errors*/ }

            if (m_errorMap.containsKey(errorNo)) {
                compiler.error(
                    line, "Multiple ON statements for error `" + errorNo + "'"
                );
            }

            String errorLabel = m.group(2);
            m_errorMap.put(errorNo, errorLabel);
        }

        //Extract the default statement (if there is one).
        m = Pattern.compile(m_defaultReStr).matcher(arguments);
        if (m.find()) {
            m_defaultLabel = m.group(1);
            if (m.find()) {
                compiler.error(line, "Multiple DEFAULT statements.");
            }
        } else {
            m_defaultLabel = null;
        }
    }

    @Override
    public void checkLabels() {
        for (String label : m_errorMap.values()) {
            Label.checkExists(m_compiler, m_line, label);
        }
    }

}

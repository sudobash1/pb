package command;

import pbsc.*;
import java.util.*;
import java.util.regex.*;

public class CommandFactory {

    private Compiler = m_compiler;

    //RegExs =================================================================

    private final String m_commandReStr = "^([a-zA-Z]+)\\s"
    private Pattern m_commandRe = null;
    
    public CommandFactory(Compiler compiler) {
        m_compiler = compiler;

        //Initialize the RegExs
        m_commandRe = Pattern.compile(m_commandReStr); 
    }

    public GenerateCommand(String s, int line) {
        if (s.trim == "") { return null; }
        Matcher commandMatcher = m_commandRe.matcher(s);

        if (! m.matches()) {
            m_compiler.errorMsgr().error(line, "Malformed line.")
        }
    }

}

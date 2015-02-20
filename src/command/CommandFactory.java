package command;

import pbsc.*;
import java.util.*;
import java.util.regex.*;

public class CommandFactory {

    private PbscCompiler m_compiler;

    //RegExs =================================================================

    private final String m_commandReStr = "^([a-zA-Z]+)\\s";
    private Pattern m_commandRe = null;

    private final String m_commandArgsReStr = "^[a-zA-Z]+\\s(.*)$";
    private Pattern m_commandArgsRe = null;

    public CommandFactory(PbscCompiler compiler) {
        m_compiler = compiler;

        //Initialize the RegExs
        m_commandRe = Pattern.compile(m_commandReStr); 
        m_commandArgsRe = Pattern.compile(m_commandArgsReStr); 
    }
    
    public Command GenerateCommand(String s, int line) {

        //Remove newlines and tabs. And trim
        s=s.replaceAll("\\s" , " ").trim();

        //Check if this line is empty
        if (s.equals("")) { return null; }

        Matcher commandMatcher = m_commandRe.matcher(s);
        Matcher argsMatcher = m_commandArgsRe.matcher(s);

        if (! commandMatcher.find() || ! argsMatcher.find()) {
            m_compiler.error(line, "Malformed line.");
            return null;
        }

        String commandName = commandMatcher.group(1).toLowerCase();
        String commandArgs = argsMatcher.group(1);
        String cleanCommandArgs = commandArgs.toLowerCase().trim();

        switch (commandName) {
            case "define":
                return new Define(m_compiler, line, cleanCommandArgs);
            case "int":
                return new IntDefinition(m_compiler, line, cleanCommandArgs);
            case "list":
                return new ListDefinition(m_compiler, line, cleanCommandArgs);
            case "rem":
                return new Rem(m_compiler, line, commandArgs);
            default: 
                m_compiler.error(line, "Invalid command `" + commandName + "'");
                return null;
        }
    }

}

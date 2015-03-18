package command;

import pbsc.*;

/**
 * An abstract class which represents a variable declaration.
 * It holds a pointer to the variable contents in memory. It should be placed
 * in the variable table in PbscCompiler.
 * @see PbscCompiler.registerNewVariable
 */
public abstract class VariableDefinition extends Command {

    /**
     * The address in memory this variable is at.
     * This is bound later in the compile process, after the parse.
     */
    protected int m_address;

    /**
     * Create a VariableDefinition.
     * @param compiler The main instance of the PbscCompiler.
     * @param line The line the command was found on.
     */
    public VariableDefinition(PbscCompiler compiler, int line) {
        super(compiler, line);

        if (currentSub != null) {
            currentSub.localVars.add(this);
        }
    }

    /**
     * Returns the address in memory this variable is at.
     * @return The address in memory this variable is at.
     */
    public int getAddress(){
        return m_address;
    }

    /**
     * This should be called as soon as it is known where this variable will
     * be located.
     * This happens after the parse is completed in the main compiler class.
     * @param address The address of this variable in memory.
     */
    public void setAddress(int address){
        m_address = address;
    }

    /**
     * Get the number of bytes needed to store this variable.
     * @return the number of bytes needed to store this variable.
     */
    public abstract int getSize();

    /**
     * Generates the code to store this variable to the stack.
     * @return The pidgen code to store this variable to the stack.
     */
    public abstract String pushVar();

    /**
     * Generates the code to restore this variable from the stack.
     * @return The pidgen code to restore this variable from the stack.
     */
    public abstract String popVar();
}

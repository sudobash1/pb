package command;

import pbsc.*;

public abstract class VariableDefinition extends Command {

    //The address in memory this variable is at.
    //This is bound later in the compile process, after the parse.
    protected int m_address;

    public VariableDefinition(PbscCompiler compiler, int line) {
        super(compiler, line);
    }

    public int getAddress(){
        return m_address;
    }

    public int setAddress(){
        return m_address;
    }

    //Get the number of bytes needed to store this Variable.
    public abstract int getSize();

}

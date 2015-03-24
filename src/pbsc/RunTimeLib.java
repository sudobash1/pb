package pbsc;

import java.util.*;
import command.*;
import expression.*;

/**
 * A class to generate the needed parts of the runtime lib.
 */
public class RunTimeLib {

    /**The main instance of the PbscCompiler.*/
    private PbscCompiler m_compiler = null;

    private ArrayList<PbscCompiler.RunTimeLibrary> m_enabledSubroutines;


    private String clearStackLabel;
    private String clearStackEndLabel;
    private String clearStackExitLabel;
    private String clearStackLoopLabel;

    /**
     * Instantiate the RunTimeLib.
     * @param compiler The main instance of the PbscCompiler.
     */
    protected RunTimeLib(PbscCompiler compiler) {
        m_compiler = compiler;
        m_enabledSubroutines = new ArrayList<PbscCompiler.RunTimeLibrary>();

        //Initialize labels
        //TODO why isn't applyMagic working properly?
        clearStackLabel =
            m_compiler.applyMagic("RUNTIMELIB_CLEARSTACK");
        clearStackEndLabel =
            m_compiler.applyMagic("RUNTIMELIB_CLEARSTACK_END");
        clearStackExitLabel =
            m_compiler.applyMagic("RUNTIMELIB_CLEARSTACK_EXIT");
        clearStackLoopLabel =
            m_compiler.applyMagic("RUNTIMELIB_CLEARSTACK_LOOP");
    }

    /**
     * Return code to call specified runtime library method.
     * @param method The runtime library method call
     * @return The Pidgen code to call the runtime library method.
     */
    protected String callRuntimeMethod(PbscCompiler.RunTimeLibrary method) {
        m_enabledSubroutines.add(method);

        String ret = "PUSH R" + PbscCompiler.pcRegister + endl();

        switch (method) {
            case CLEAR_STACK:
                ret += "BRANCH " + clearStackLabel + endl();
                break;
        }

        return ret;
    }

    /**
     * Return the line ending.
     */
    private final String endl() {
        return m_compiler.lineEnding();
    }

    /**The destination register for the LValue*/
    public final static int LRegister = 3;
    /**The destination register for the RValue*/
    public final static int RRegister = 4;

    /**A temp register. Clobber at will.*/
    public final static int tmpRegister0 = 0;
    /**A temp register. Clobber at will.*/
    public final static int tmpRegister1 = 1;
    /**A temp register. Clobber at will.*/
    public final static int tmpRegister2 = 2;
    
    /**
     * Generate the pidgen code for the Run Time Library to be included at
     * the top of the output piden code.
     */
    protected String generateCode() {
        String ret = "";

        if (m_enabledSubroutines.size() > 0) {

            if (m_enabledSubroutines.contains(
                    PbscCompiler.RunTimeLibrary.CLEAR_STACK))
            {                

                VariableDefinition stackVariableDef =
                    m_compiler.runTimeLibIntVariable("StackSize");

                IntVariablePointer stackVariable = new IntVariablePointer(
                    m_compiler, -1, LRegister, stackVariableDef
                );
                    
                //This will need to be changed depending on how the stack grows
                ret += 
                    stackVariable.generateCode() +
                    "SAVE R" + m_compiler.spRegister + " R" + LRegister +
                    endl() +
                    "BRANCH " + clearStackEndLabel + endl() +

                    ":" + clearStackLabel + endl() +
                    //Grap the return address
                    "POP R" + tmpRegister2 + endl() +

                    //Find where the stack begins
                    stackVariable.generateCode() +
                    "LOAD R" + tmpRegister0 + " R" + LRegister + endl() +
                    "SET R" + tmpRegister1 + " 1" + endl() +
                    "SUB R" + tmpRegister0 + " R" + tmpRegister0 + " R" +
                    tmpRegister1 + endl() +

                    ":" + clearStackLoopLabel + endl() +
                    
                    "BLT R" + tmpRegister0 + " R" + m_compiler.spRegister +
                    " " + clearStackExitLabel + endl() +
                    "POP R" + tmpRegister1 + endl() +
                    "BRANCH " + clearStackLoopLabel + endl() +

                    ":" + clearStackExitLabel + endl() +

                    "SET R" + tmpRegister1 + " " + m_compiler.INSTSIZE +
                    endl() +
                    "ADD R" + m_compiler.pcRegister  + " R" + tmpRegister2 +
                    " R" + tmpRegister1 + endl()+

                    ":" + clearStackEndLabel + endl();
            }
        }
        return ret;
    }
}

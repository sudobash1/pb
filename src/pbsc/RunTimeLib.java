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
     * Generate code to call specified runtime library method.
     * @param method The runtime library method call
     */
    protected void callRuntimeMethod(PbscCompiler.RunTimeLibrary method) {
        m_enabledSubroutines.add(method);

        write("PUSH", PbscCompiler.pcRegister);

        switch (method) {
            case CLEAR_STACK:
                write("BRANCH", clearStackLabel);
                break;
        }
    }

    /**
     * Return the line ending.
     */
    private final String endl() {
        return m_compiler.lineEnding();
    }

    /**The destination register for the LValue*/
    public final static String LRegister = "R3";
    /**The destination register for the RValue*/
    public final static String RRegister = "R4";

    /**A temp register. Clobber at will.*/
    public final static String tmpRegister0 = "R0";
    /**A temp register. Clobber at will.*/
    public final static String tmpRegister1 = "R1";
    /**A temp register. Clobber at will.*/
    public final static String tmpRegister2 = "R2";
    
    /**
     * Writes a line to the output file.
     */
    protected final void write(String s1) {
        m_compiler.write(s1);
    }

    /**
     * Writes a line to the output file.
     */
    protected final void write(String s1, String s2) {
        m_compiler.write(s1, s2);
    }

    /**
     * Writes a line to the output file.
     */
    protected final void write(String s1, String s2, String s3) {
        m_compiler.write(s1, s2, s3);
    }

    /**
     * Writes a line to the output file.
     */
    protected final void write(String s1, String s2, String s3, String s4) {
        m_compiler.write(s1, s2, s3, s4);
    }

    /**
     * Writes a line to the output file.
     */
    protected final void write(
        String s1, String s2, String s3, String s4, String s5
    ) {
        m_compiler.write(s1, s2, s3, s4, s5);
    }

    /**
     * Generate the pidgen code for the Run Time Library to be included at
     * the top of the output piden code.
     */
    protected void generateCode() {
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
                stackVariable.generateCode();
                write("SAVE", m_compiler.spRegister, LRegister);
                write("BRANCH", clearStackEndLabel);
                write(":" + clearStackLabel);

                //Grab the return address
                write("POP", tmpRegister2);

                //Find where the stack begins
                stackVariable.generateCode();
                write("LOAD", tmpRegister0, LRegister);
                write("SET", tmpRegister1, "1");
                write("SUB", tmpRegister0, tmpRegister0, tmpRegister1);
                write(":" + clearStackLoopLabel);
                write(
                    "BLT", tmpRegister0, m_compiler.spRegister,
                    clearStackExitLabel
                );
                write("POP", tmpRegister1);
                write("BRANCH", clearStackLoopLabel);
                write(":" + clearStackExitLabel);

                write("SET", tmpRegister1, ""+m_compiler.INSTSIZE);
                write(
                    "ADD", m_compiler.pcRegister, tmpRegister2, tmpRegister1
                );

                write(":" + clearStackEndLabel);
            }
        }
    }
}

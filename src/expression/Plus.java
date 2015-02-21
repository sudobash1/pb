package expression;

import java.util.*;
import pbsc.*;

public class Plus extends LispExpression {

    public Plus(PbscCompiler compiler, int line, ArrayList<Expression> operands) {
        super(compiler, line, operands);
        
        if (operands.size() != 2) {
            compiler.error(
                line,
                "Wrong number of arguments to (+). Requires 2, found " +
                operands.size() + "."
            );
        }
    }

    @Override
    public String generateCode() {
        return ""; //XX
    }

    @Override
    public int stackReq() {
        return 0; //XXX
    }

    @Override
    public boolean canPlaceInRegister() {
        return false;
    }

}

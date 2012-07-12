package eu.heliovo.clientapi.config.des;

import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.EQ;
import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.GT;
import static eu.heliovo.clientapi.config.des.DesFunctionArgument.DesFunctionOperator.LT;

public class DesFunctionDeriv extends DesFunction {
    
    public DesFunctionDeriv() {
        super("DERIV", "Parameter Derivative", "PARAM(t+dt) - PARAM(t) OP DELTAF; dt = AVERAGETIME * 2", 1);
//        this.addParam(v);
//        this.addParam(n);
//        this.addParam(b);
//        this.addParam(bx);
//        this.addParam(by);
//        this.addParam(bz);
        this.addFunctionArgument(new DesFunctionArgument("DELTAF", LT, GT));
        DesFunctionArgument arg = this.addFunctionArgument(new DesFunctionArgument("AVERAGETIME", EQ));
        arg.setCompound(new DesFunctionArgumentCompound("DELTAT", "*2"));

    }
    
    
    
    
    
}

package xyz.cofe.stsl.eval.sample;

import xyz.cofe.stsl.eval.TastInterop;

public class ScriptComputeImpl1 extends ScriptCompute1 {
    public ScriptComputeImpl1( TastInterop interop ){
        this.interop = interop;
    }

    private TastInterop interop;

    @Override
    public int sum(){
        interop.setVariable("a", a, int.class);
        return (Integer) (interop.computeField("sum"));
    }
}

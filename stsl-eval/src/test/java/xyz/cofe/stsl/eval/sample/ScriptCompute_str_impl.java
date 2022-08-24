package xyz.cofe.stsl.eval.sample;

import xyz.cofe.stst.eval.TastInterop;
import xyz.cofe.stst.eval.eval;
import xyz.cofe.stst.eval.export;

public class ScriptCompute_str_impl extends ScriptCompute_str {
    private TastInterop interop;
    @Override
    public String sum(){
        interop.setVariable("a", a, String.class);
        interop.setVariable("b", b, String.class);
        return (String)interop.computeField("sum");
    }
}

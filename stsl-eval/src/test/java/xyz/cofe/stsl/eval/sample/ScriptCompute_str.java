package xyz.cofe.stsl.eval.sample;

import xyz.cofe.stst.eval.eval;
import xyz.cofe.stst.eval.export;

public abstract class ScriptCompute_str {
    @export public String a;
    @export public String b;
    @eval public abstract String sum();
}

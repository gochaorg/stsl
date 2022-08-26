package xyz.cofe.stsl.eval.sample;

import xyz.cofe.stsl.eval.eval;
import xyz.cofe.stsl.eval.export;

public abstract class ScriptCompute_str {
    @export public String a;
    @export public String b;
    @eval public abstract String sum();
}

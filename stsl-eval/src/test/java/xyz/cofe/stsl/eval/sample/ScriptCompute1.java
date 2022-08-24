package xyz.cofe.stsl.eval.sample;

import xyz.cofe.stst.eval.eval;
import xyz.cofe.stst.eval.export;

public abstract class ScriptCompute1 {
    @export public int a;
    @export public int b;
    @eval public abstract int sum();
}

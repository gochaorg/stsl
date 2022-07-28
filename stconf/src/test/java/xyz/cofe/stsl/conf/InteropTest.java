package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.Test;
import xyz.cofe.jvmbc.ByteCode;
import xyz.cofe.jvmbc.cls.CBegin;
import xyz.cofe.stst.conf.Interop;

public class InteropTest extends CommonForTest {
    @Test
    public void dump01(){
        var cbegin = CBegin.parseByteCode(ScriptCompute1.class);
        dump(cbegin);
    }

    @Test
    public void dump01impl(){
        var cbegin = CBegin.parseByteCode(ScriptCompute1Impl.class);
        dump(cbegin);
    }

    @Test
    public void compile01(){
        var interop = new Interop();
        interop.compile(ScriptCompute1.class);
    }
}

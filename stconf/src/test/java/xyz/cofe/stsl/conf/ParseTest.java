package xyz.cofe.stsl.conf;

import org.junit.jupiter.api.Test;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.shade.scala.Option;
import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.tast.TASTDump;
import xyz.cofe.stsl.tast.Toaster;
import xyz.cofe.stsl.tast.TypeScope;
import xyz.cofe.stsl.tast.VarScope;

public class ParseTest {
    @Test
    public void sample01(){
        System.out.println("hello");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        Option<AST> ast = Parser.defaultParser().parse("20 + 20 / 2");
        ASTDump.dump(ast.get());

        VarScope varScope = new VarScope();

        Toaster toaster = Toaster.defaultToaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        TASTDump.dump(tast);

        System.out.println("compiled result type="+tast.supplierType());

        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }
}

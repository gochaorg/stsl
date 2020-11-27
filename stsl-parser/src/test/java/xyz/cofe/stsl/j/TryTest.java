package xyz.cofe.stsl.j;

import org.junit.jupiter.api.Test;
import scala.Option;
import scala.collection.immutable.List;
import scala.collection.immutable.Seq;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.tast.*;
import xyz.cofe.stsl.types.CallableFn;
import xyz.cofe.stsl.types.Fun;

public class TryTest {
    @Test
    public void test01(){
        System.out.println("test01");
        System.out.println("============");

        TypeScope ts = new TypeScope();
        List<Fun> lst = JvmType.implicitConversion().mapConserve( (f) -> (Fun)f);
        ts.implicits_$eq( lst );

        Option<AST> ast = Parser.parse("20 + 20 / 2");
        ASTDump.dump(ast.get());

        VarScope varScope = new VarScope();

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        TASTDump.dump(tast);

        System.out.println("compiled result type="+tast.supplierType());

        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }
}

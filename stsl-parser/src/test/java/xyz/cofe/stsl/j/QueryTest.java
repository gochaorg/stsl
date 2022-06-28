package xyz.cofe.stsl.j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scala.Option;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.tast.*;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.TypeDescriber;
import xyz.cofe.stsl.types.TypeVarReplacer;
import static xyz.cofe.stsl.j.TryTest.listType;
import static xyz.cofe.stsl.j.TryTest.personType;

import java.util.Arrays;
import java.util.function.Supplier;

public class QueryTest {
    @Test
    public void query01(){
        System.out.println("query01");
        System.out.println("========================");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());
        ts.imports(Arrays.asList(listType, personType));

        VarScope varScope = new VarScope();

        TryTest.TList<TryTest.Person> personTList1 = new TryTest.TList<>();
        personTList1.add(new TryTest.Person("Vasia",10));
        personTList1.add(new TryTest.Person("Masha",12));
        personTList1.add(new TryTest.Person("Petia",13));
        personTList1.add(new TryTest.Person("Vlad",14));
        personTList1.add(new TryTest.Person("Vladimir",15));
        personTList1.add(new TryTest.Person("Alexandr",17));

        TObject listPersonType = listType.typeVarReplacer( (TypeVarReplacer a) -> a.set("A",personType) );
        varScope.define("lst", listPersonType, personTList1);

        Option<AST> ast = new Parser(false).parse("lst.size");
        System.out.println("ast:");
        Assertions.assertTrue(ast.isDefined());
        ASTDump.dump(ast.get());

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());

        System.out.println("result type:");
        System.out.println(TypeDescriber.describe(tast.supplierType()));

        System.out.println("compute:");
        Supplier<Object> result = tast.supplier();
        Assertions.assertTrue(result!=null);
        System.out.println(result.get());
    }

    @Test
    public void query02(){
        System.out.println("query02");
        System.out.println("========================");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());
        ts.imports(Arrays.asList(listType, personType));

        VarScope varScope = new VarScope();

        TryTest.TList<TryTest.Person> personTList1 = new TryTest.TList<>();
        personTList1.add(new TryTest.Person("Vasia",10));
        personTList1.add(new TryTest.Person("Masha",12));
        personTList1.add(new TryTest.Person("Petia",13));
        personTList1.add(new TryTest.Person("Vlad",14));
        personTList1.add(new TryTest.Person("Vladimir",15));
        personTList1.add(new TryTest.Person("Alexandr",17));

        TObject listPersonType = listType.typeVarReplacer( (TypeVarReplacer a) -> a.set("A",personType) );
        varScope.define("lst", listPersonType, personTList1);

        Option<AST> ast = new Parser(false).parse(
            "{ " +
                "a: lst.size, " +
                "b: lst.size + lst.size, " +
                "c: { a: 1 } " +
                "}"
        );
        //System.out.println("\nast:");
        //Assertions.assertTrue(ast.isDefined());

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        //System.out.println("\ntast:");
        //TASTDump.dump(tast);

        System.out.println("\nresult type:");
        System.out.println(TypeDescriber.describe(tast.supplierType()));

        System.out.println("\ncompute:");
        Supplier<Object> result = tast.supplier();
        Assertions.assertTrue(result!=null);
        System.out.println(result.get());
    }
}

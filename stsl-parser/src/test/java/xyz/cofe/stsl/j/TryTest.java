package xyz.cofe.stsl.j;

import org.junit.jupiter.api.Test;
import scala.Function1;
import scala.Function2;
import scala.Option;
import scala.Tuple2;
import scala.collection.immutable.List;
import scala.collection.immutable.Seq;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.tast.*;
import xyz.cofe.stsl.types.CallableFn;
import xyz.cofe.stsl.types.Fn;
import xyz.cofe.stsl.types.Fun;
import xyz.cofe.stsl.types.Param;
import xyz.cofe.stsl.types.Params;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;

public class TryTest {
    @Test
    public void test01(){
        System.out.println("test01");
        System.out.println("============");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

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

    @Test
    public void simpleNumericVars(){
        System.out.println("simpleNumericVars");
        System.out.println("=================");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        Option<AST> ast = Parser.parse("a + b * c");
        ASTDump.dump(ast.get());

        VarScope varScope = new VarScope();
        varScope.define("a", JvmType.INT(), 1);
        varScope.define("b", JvmType.INT(), 2);
        varScope.define("c", JvmType.INT(), 3);

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        TASTDump.dump(tast);

        System.out.println("compiled result type="+tast.supplierType());

        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }
    
    public static String repeat( String string, int count ){
        if( string==null )return null;
        if( count<=0 )return "";
        if( count==1 )return string;
        StringBuilder sb = new StringBuilder();
        for( int i=0; i<count; i++ ){
            sb.append(string);
        }
        return sb.toString();
    }
    
    @Test
    public void namedFuncCall(){
        System.out.println("namedFuncCall");
        System.out.println("=================");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        Option<AST> ast = Parser.parse("repeat( a, b )");
        ASTDump.dump(ast.get());

        //Param aParam = new Param("str", JvmType.STRING());
        //Param bParam = new Param("count", JvmType.INT());
        //Params params = new Params(List.canBuildFrom().);
        Params.create("str", JvmType.STRING()).add("count", JvmType.INT()).build();
        CallableFn callFn = Fn.create(
            Params.create("str", JvmType.STRING()).add("count", JvmType.INT()).build(),
            JvmType.STRING()
        ).callable( args -> repeat((String)args.get(0), (Integer)args.get(1)) );

        VarScope varScope = new VarScope();
        varScope.define("repeat", callFn, callFn);
        varScope.define("a", JvmType.STRING(), "abc");
        varScope.define("b", JvmType.INT(), 3);

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        TASTDump.dump(tast);

        System.out.println("compiled result type="+tast.supplierType());

        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }
    
    public static class Person {
        public String name;
        public int age;
        public Person(){}
        public Person(String name, int age){
            this.name = name;
            this.age = age;
        }
        public boolean allow( String actionName ){
            if( actionName==null )return false;
            return actionName.length()>3;
        }
    }

    public TObject personType = TObject.create("Person").fileds( fieldsBuilder -> {
        fieldsBuilder
            .fileld("name", JvmType.STRING())
            .writeable(
                obj -> ((Person)obj).name,
                (obj,value) -> ((Person)obj).name = (String)value )
            .add()
            .fileld("age", JvmType.INT())
            .writeable(
                obj -> ((Person)obj).age,
                (obj,value) -> ((Person)obj).age = (int)value )
            .add();
    }).methods( methodsBuilder -> {
        methodsBuilder.method( mbuilder -> {
            mbuilder
                .name("allow")
                .params( param -> param.param("obj", Type.THIS()).param("name", JvmType.STRING()) )
                .result( JvmType.BOOLEAN() )
                .callable( args -> ((Person)args.get(0)).allow((String)args.get(1)) )
                .add()
            ;
        });
    }).build();
    { personType.freeze(); }

    @Test
    public void objectProperties(){
        System.out.println("objectProperties");
        System.out.println("=================");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        ts.imports(personType);

        Option<AST> ast = Parser.parse("a.age + b * c");
        ASTDump.dump(ast.get());

        VarScope varScope = new VarScope();
        varScope.define("a", personType, new Person("Masha", 12));
        varScope.define("b", JvmType.INT(), 2);
        varScope.define("c", JvmType.INT(), 3);

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        TASTDump.dump(tast);

        System.out.println("compiled result type="+tast.supplierType());

        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }

    @Test
    public void objectMethods(){
        System.out.println("objectMethods");
        System.out.println("=================");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        ts.imports(personType);

        Option<AST> ast = Parser.parse("a.allow( \"abcd\" )");
        ASTDump.dump(ast.get());

        VarScope varScope = new VarScope();
        varScope.define("a", personType, new Person("Masha", 12));
        varScope.define("b", JvmType.INT(), 2);
        varScope.define("c", JvmType.INT(), 3);

        Toaster toaster = new Toaster(ts,varScope);
        TAST tast = toaster.compile(ast.get());
        TASTDump.dump(tast);

        System.out.println("compiled result type="+tast.supplierType());

        Object result = tast.supplier().get();
        System.out.println("compiled result value="+result);
    }
}

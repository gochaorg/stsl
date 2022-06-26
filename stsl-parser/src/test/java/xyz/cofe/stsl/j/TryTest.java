package xyz.cofe.stsl.j;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scala.Option;
import xyz.cofe.stsl.ast.AST;
import xyz.cofe.stsl.ast.ASTDump;
import xyz.cofe.stsl.ast.Parser;
import xyz.cofe.stsl.tast.JvmType;
import xyz.cofe.stsl.tast.*;
import xyz.cofe.stsl.types.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@SuppressWarnings("rawtypes")
public class TryTest {
    @Test
    public void test01(){
        System.out.println("test01");
        System.out.println("============");

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        Option<AST> ast = new Parser().parse("20 + 20 / 2");
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

        Option<AST> ast = new Parser().parse("a + b * c");
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

        Option<AST> ast = new Parser().parse("repeat( a, b )");
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

    //region Person
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

    public static TObject personType = TObject.create("Person").fileds( fieldsBuilder -> {
        fieldsBuilder.field("name",JvmType.STRING());

        fieldsBuilder
            .field("name", JvmType.STRING())
            .writeable(
                obj -> ((Person)obj).name,
                (obj,value) -> ((Person)obj).name = (String)value )
            .add()
            .field("age", JvmType.INT())
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
    static { personType.freeze(); }
    //endregion

    @Test
    public void objectProperties(){
        System.out.println("objectProperties");
        System.out.println("=================");

        System.out.println(TypeDescriber.describe(personType));

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        ts.imports(personType);

        Option<AST> ast = new Parser().parse("a.age + b * c");
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

        Option<AST> ast = new Parser().parse("a.allow( \"abcd\" )");
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

    //region TList
    public static class TList<A> implements Iterable<A> {
        private final java.util.List<A> list = new java.util.ArrayList<A>();
        public TList(){
        }
        public TList(Iterable<A> itr){
            if( itr!=null ){
                for( A a : itr ){
                    list.add(a);
                }
            }
        }

        @Override
        public Iterator<A> iterator(){
            return list.iterator();
        }

        public int size(){
            return list.size();
        }

        public A get(int idx){
            return list.get(idx);
        }

        public void add(A a){
            list.add(a);
        }

        public void add(TList<A> a){
            if( a==null )throw new IllegalArgumentException( "a==null" );
            list.addAll(a.list);
        }

        public TList<A> filter(Predicate<A> filter){
            if( filter==null )throw new IllegalArgumentException("filter==null");
            return new TList<>( list.stream().filter(filter).collect(Collectors.toList()) );
        }

        public <B> TList<B> map(Function<A,B> mapper){
            if( mapper==null )throw new IllegalArgumentException( "mapper==null" );
            return new TList<>( list.stream().map(mapper).collect(Collectors.toList()) );
        }

        @Override
        public String toString(){
            return list.toString();
        }
    }

    public static TObject listType = TObject.create("TList")
        .fileds( f -> {
            f.field("size", JvmType.INT())
                .writeable( obj -> ((TList)obj).size(),
                    (obj,value) -> { return null; } ).add();
        })
        .build();

    static {
        listType.generics().append( new AnyVariant("A") );
        TObject.build(listType).methods( mths -> {
            mths.method( mth -> {
                mth.name("add").result(Type.VOID()).params( prm -> {
                    prm.param("self", Type.THIS());
                    prm.param("idx", TypeVariable.apply("A", Type.THIS()));
                }).callable( args->
                    { //noinspection unchecked
                        ((TList)args.get(0)).add( args.get(1) ); return null; }
                ).add();

                mth.name("get").result( TypeVariable.apply("A", Type.THIS())).params( prm -> {
                    prm.param("self", Type.THIS());
                    prm.param("idx", JvmType.INT());
                }).callable( args->
                    ((TList)args.get(0)).get( (Integer)args.get(1))
                ).add();

                mth.name("filter").result( Type.THIS() ).params( prm -> {
                    Fn pred = Fn.create().returns(Type.THIS()).params( p->p.add("a",TypeVariable.apply("A", Type.THIS())) ).build();
                    prm.param("self", Type.THIS());
                    prm.param("idx", pred);
                }).add();

                Fn mapper = Fn.create()
                    .generics( g->g.any("B") )
                    .returns( new TypeVariable("B",Type.FN()) )
                    .params( p->p.add("a",TypeVariable.apply("A", Type.THIS())) )
                    .build();

                mth.name("map")
                    .generics( g->g.any("B") )
                    .result( GenericInstance.set("A", new TypeVariable("B", Type.FN())).build(listType) )
                    .params( prm->{
                        prm.param("self", Type.THIS());
                        prm.param("mapper", mapper );
                    })
                    .callable( args -> {
                        Object oFn = args.get(1);
                        //((TList)args.get(0)).map(  );
                        CallableFn cFn = (CallableFn) oFn;
                        Object mapRes =
                        ((TList)args.get(0)).map( itm -> {
                            ArrayList<Object> arg = new ArrayList<>();
                            arg.add(itm);
                            Object res = cFn.call(arg);
                            return res;
                        });
                        return mapRes;
                    })
                    .add();
            });
        });
    }
    //endregion

    @Test
    public void listTypeShow(){
        System.out.println("listTypeShow");
        System.out.println("========================");

        System.out.println(TypeDescriber.describe(listType));

        TObject userListType = listType.typeVarReplacer( (TypeVarReplacer a) -> a.set("A",personType) );
        System.out.println(TypeDescriber.describe(userListType));
    }

    @Test
    public void mapClosureTypeInference(){
        System.out.println("mapClosureTypeInference");
        System.out.println("=============================");

        TObject personListType = listType.typeVarReplacer( (TypeVarReplacer a) -> a.set("A",personType) );
        TList<Person> personTList = new TList<>();
        personTList.add(new Person());

        TypeScope ts = new TypeScope();
        ts.setImplicits(JvmType.implicitConversion());

        ts.imports(Arrays.asList(listType, personListType, personType));
        String mapLambdaSrc = "lst.map( x : "+personType.name()+" => x.name )";

        System.out.println("parse: "+mapLambdaSrc);
        Option<AST> ast = new Parser().parse(mapLambdaSrc);

        System.out.println("ast:");
        ASTDump.dump(ast.get());
        Assertions.assertTrue(ast.isDefined());

        VarScope varScope = new VarScope();

        TList<Person> personTList1 = new TList<>();
        personTList1.add(new Person("Vasia",10));
        personTList1.add(new Person("Masha",12));
        personTList1.add(new Person("Petia",13));
        personTList1.add(new Person("Vlad",14));
        personTList1.add(new Person("Vladimir",15));
        personTList1.add(new Person("Alexandr",17));

        varScope.define("lst", personListType, personTList1);

        Toaster toaster = new Toaster(ts,varScope);
        TAST mapTast = toaster.compile(ast.get());

        System.out.println("tast:");
        TASTDump.dump(mapTast);

        System.out.println("result type:");
        System.out.println(TypeDescriber.describe(mapTast.supplierType()));

        System.out.println("compute:");
        Supplier<Object> mapResult = mapTast.supplier();
        Assertions.assertTrue(mapResult!=null);

        System.out.println(mapResult.get());
    }
}

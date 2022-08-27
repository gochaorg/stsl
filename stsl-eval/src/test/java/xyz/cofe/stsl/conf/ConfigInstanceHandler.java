package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.types.Field;
import xyz.cofe.stsl.types.GenericInstance;
import xyz.cofe.stsl.types.Obj;
import xyz.cofe.stsl.types.Type;
import xyz.cofe.stsl.types.WriteableField;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

class ConfigInstanceHandler implements InvocationHandler {
    public final Class<?> confClass;

    public ConfigInstanceHandler( Class<?> conf ){
        if( conf == null ) throw new IllegalArgumentException("conf==null");
        this.confClass = conf;
    }

    private Type tastType;
    private Object computed;

    public void setTAST( TAST tast ){
        if( tast != null ){
            tastType = tast.supplierType();
            computed = tast.supplier().get();
        } else {
            tastType = null;
            computed = null;
        }
    }

    public void setTAST( Object computed, Type tastType ){
        this.tastType = tastType;
        this.computed = computed;
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable{
        if( method == null ) throw new IllegalArgumentException("method==null");
        if( method.getDeclaringClass().equals(Object.class) ){
            // Базовые методы
            switch( method.getName() ){
                case "wait":
                    if( args == null ){
                        this.wait();
                        return null;
                    } else {
                        if( args.length == 0 ){
                            this.wait();
                            return null;
                        } else if( args.length == 1 ){
                            this.wait((Long) args[0]);
                            return null;
                        } else if( args.length == 2 ){
                            this.wait((Long) args[0], (Integer) args[1]);
                            return null;
                        }
                    }
                    throw new RuntimeException("can't invoke " + method);
                case "hashCode":
                    return this.hashCode();
                case "toString":
                    return this.toString();
                case "equals":
                    if( args != null && args.length == 1 ){
                        return this.equals(args[0]);
                    }
                    throw new RuntimeException("can't invoke " + method);
                case "clone":
                    return this.clone();
                case "notify":
                    this.notify();
                    return null;
                case "notifyAll":
                    this.notifyAll();
                    return null;
                default:
                    throw new RuntimeException("can't invoke " + method);
            }
        }

        if( method.getDeclaringClass().equals(confClass) ){
            // вызов скрипта
            return read(method, args);
        }

        throw new RuntimeException("can't invoke " + method);
    }

    private Object read( Method method, Object[] args ) throws Throwable{
        if( tastType == null ) throw new IllegalStateException("tastType (tast) not set");
        if( computed == null ) throw new IllegalStateException("computed not set");
        if( args == null || args.length == 0 ){
            // read field
            if( tastType instanceof Obj ){
                var tobj = (Obj) tastType;
                return readField(method, computed, tobj, method.getName());
            }
        }
        return null;
    }

    private Optional<Field> findField( Obj objType, String name ){
        var oFld = objType.publicFields().find(fld -> name.equals(fld.name()));
        if( oFld.isDefined() ){
            return Optional.of(oFld.get());
        } else {
            return Optional.empty();
        }
    }

    private Optional<WriteableField> findWriteableField( Obj objType, String name ){
        return findField(objType, name).flatMap(fld -> {
            if( fld instanceof WriteableField ){
                return Optional.of((WriteableField) fld);
            } else {
                return Optional.empty();
            }
        });
    }

    private final Predicate<java.lang.reflect.Type> anonObject =
        t -> t instanceof Class<?> && ((Class<?>) t).isInterface();

    private final Predicate<java.lang.reflect.Type> internalType = t ->
    {
        if( t == List.class ) return true;
        if( t == Optional.class ) return true;
        if( t instanceof ParameterizedType ){
            var pt = (ParameterizedType) t;
            var rt = pt.getRawType();
            if( rt == List.class ) return true;
            //noinspection RedundantIfStatement
            if( rt == Optional.class ) return true;
        }
        return false;
    };

    // если тип результата метода - интерфейс, создаем прокси
    private Optional<Object> proxyAnonObject( Object value, Method method, WriteableField field ){
        if( anonObject.test(method.getGenericReturnType()) ){
            var proxyClass = method.getReturnType();
            var handler = new ConfigInstanceHandler(proxyClass);
            handler.tastType = field.tip();
            handler.computed = value;
            var proxy = Proxy.newProxyInstance(confClass.getClassLoader(), new Class[]{proxyClass}, handler);
            return Optional.of(proxy);
        }
        return Optional.empty();
    }

    private Optional<Object> proxyListOfAnonObject( Object value, Method method, WriteableField field ){
        if( !(value instanceof List) ) return Optional.empty();

        var ret = method.getGenericReturnType();
        if( !(ret instanceof ParameterizedType) ) return Optional.empty();

        var pret = (ParameterizedType) ret;
        if( pret.getRawType() != List.class ) return Optional.empty();

        var targ0 = pret.getActualTypeArguments()[0];
        if( internalType.test(targ0) ) return Optional.empty();
        if( !anonObject.test(targ0) ) return Optional.empty();

        if( !(field.tip() instanceof GenericInstance) ) return Optional.empty();
        var giType = (GenericInstance) field.tip();

        var listTypeArgOpt = giType.recipe().get("A"); // todo завязано на тип stsl - List
        if( listTypeArgOpt.isEmpty() ) return Optional.empty();
        // todo добавить проверку что giType.source() - это List

        var listTypeArg = (Type) listTypeArgOpt.get();
        // todo добавить проверку что listTypeArg - это Anon

        var imList = new LazyImList<Object, Object>((List) value, sourceInst -> {
            //var proxy = ConfigInstance.create(List.class);
            //return proxy.read(listTypeArg);

            var h = new ConfigInstanceHandler((Class) targ0);
            h.setTAST(sourceInst, listTypeArg);

            var inst = Proxy.newProxyInstance(
                ((Class) targ0).getClassLoader(),
                new Class<?>[]{((Class) targ0)},
                h
            );

            return inst;
        });

        return Optional.of(imList);
    }

    private Object fieldValueMapper( Object value, Method method, WriteableField field ){
        return proxyListOfAnonObject(value, method, field).or(() -> proxyAnonObject(value, method, field)).orElse(value);
    }

    private Object readField( Method method, Object obj, Obj objType, String name ) throws Throwable{
        return findWriteableField(objType, name)
            .map(fld -> fieldValueMapper(fld.reading().apply(obj), method, fld))
            .orElseThrow();
    }
}

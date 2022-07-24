package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.tast.TAST;
import xyz.cofe.stsl.types.Field;
import xyz.cofe.stsl.types.Obj;
import xyz.cofe.stsl.types.TObject;
import xyz.cofe.stsl.types.Type;
import xyz.cofe.stsl.types.WriteableField;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Optional;

class ConfInstHandler implements InvocationHandler {
    public final Class<?> confClass;

    public ConfInstHandler( Class<?> conf ){
        if( conf == null ) throw new IllegalArgumentException("conf==null");
        this.confClass = conf;
    }

    private Type tastType;
    private Object computed;

    public void setTAST( TAST tast ){
        //this.tast = tast;
        if( tast!=null ){
            tastType = tast.supplierType();
            computed = tast.supplier().get();
        }else{
            tastType = null;
            computed = null;
        }
    }

    @Override
    public Object invoke( Object proxy, Method method, Object[] args ) throws Throwable {
        if( method == null ) throw new IllegalArgumentException("method==null");
        if( method.getDeclaringClass().equals(Object.class) ){
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
            return read(method, args);
        }

        throw new RuntimeException("can't invoke " + method);
    }

    private Object read( Method method, Object[] args ) throws Throwable {
        if( tastType==null )throw new IllegalStateException("tastType (tast) not set");
        if( computed==null )throw new IllegalStateException("computed not set");
        if( args==null || args.length==0 ){
            // read field
            if( tastType instanceof Obj ){
                var tobj = (Obj)tastType;
                return readField(method, computed, tobj, method.getName());
            }
        }
        return null;
    }

    private Optional<Field> findField( Obj objType, String name ) {
        var oFld = objType.publicFields().find( fld -> name.equals(fld.name()) );
        if( oFld.isDefined() ){
            return Optional.of(oFld.get());
        }else{
            return Optional.empty();
        }
    }
    private Optional<WriteableField> findWriteableField( Obj objType, String name ){
        return findField(objType, name).flatMap( fld -> {
            if( fld instanceof WriteableField ){
                return Optional.of( (WriteableField)fld );
            }else {
                return Optional.empty();
            }
        });
    }

    private Optional<Object> proxy( Object value, Method method, WriteableField field ){
        var retClass = method.getReturnType();
        if( retClass.isInterface() ){
            var handler = new ConfInstHandler(retClass);
            handler.tastType = field.tip();
            handler.computed = value;
            var proxy = Proxy.newProxyInstance( confClass.getClassLoader(), new Class[]{retClass}, handler );
            return Optional.of(proxy);
        }
        return Optional.empty();
    }

    private Object fieldValueMapper( Object value, Method method, WriteableField field ){
        return proxy(value,method,field).orElse(value);
    }

    private Object readField( Method method, Object obj ,Obj objType, String name ) throws Throwable {
        return findWriteableField(objType, name)
            .map( fld -> fieldValueMapper(fld.reading().apply(obj), method, fld) )
            .orElseThrow();
    }
}

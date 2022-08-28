package xyz.cofe.stsl.conf;

import xyz.cofe.stsl.conf.reflect.JvmPrimitive;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BridgeTypeChecker {
    public static class Result {
        public Result(){
        }

        public Result( String error ){
            if( error == null ) throw new IllegalArgumentException("error==null");
            this.errors.add(error);
        }

        public Result( Iterable<String> errors ){
            if( errors == null ) throw new IllegalArgumentException("errors==null");
            for( var e : errors ){
                if( e != null ){
                    this.errors.add(e);
                }
            }
        }

        public boolean ok(){
            return errors().isEmpty();
        }

        private List<String> errors = new ArrayList<>();

        public List<String> errors(){
            return Collections.unmodifiableList(errors);
        }
    }

    private static Result succ(){
        return new Result();
    }

    private static Result fail( String string ){
        if( string == null ) throw new IllegalArgumentException("string==null");
        return new Result(string);
    }

    private static Result fail( Iterable<String> strings ){
        if( strings == null ) throw new IllegalArgumentException("strings==null");
        return new Result(strings);
    }

    private enum SpecJVMType {
        Optional,
        List
    }

    private Optional<SpecJVMType> specJVMType( java.lang.reflect.Type jvmType ){
        if( jvmType == null ) throw new IllegalArgumentException("jvmType==null");
        if( !(jvmType instanceof ParameterizedType) ) return Optional.empty();
        var pt = (ParameterizedType) jvmType;
        var rt = pt.getRawType();
        if( rt == List.class ) return Optional.of(SpecJVMType.List);
        if( rt == Optional.class ) return Optional.of(SpecJVMType.Optional);
        return Optional.empty();
    }

    // x is primitive
    // x is object
    // x is optional of y
    // x is list of y
    public Result assignable( java.lang.reflect.Type jvmType, xyz.cofe.stsl.types.Type stslType ){
        if( jvmType == null ) throw new IllegalArgumentException("jvmType==null");
        if( stslType == null ) throw new IllegalArgumentException("stslType==null");

        var prim = JvmPrimitive.of(jvmType);
        if( prim.isPresent() ){
            var sType = prim.get().stslType;
            return sType.assignable(stslType) ? succ() : fail("" + jvmType + " not assignable from " + stslType);
        }

        return fail("not impl " + jvmType);
    }
}

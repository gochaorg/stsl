package xyz.cofe.stsl.conf.reflect;

import xyz.cofe.stsl.conf.text.Text;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class JvmObjContainer implements JvmType {
    public final List<JvmObjMember> members;

    public JvmObjContainer( List<JvmObjMember> members ){
        if( members == null ) throw new IllegalArgumentException("members==null");
        this.members = members;
    }

    public static Optional<JvmObjContainer> of( Type jvmType ){
        if( jvmType == null ) throw new IllegalArgumentException("jvmType==null");

        Class<?> jvmClass = (Class<?>) jvmType;
        if( !jvmClass.isInterface() ) return Optional.empty();
        if( jvmClass.isAnonymousClass() ) return Optional.empty();
        if( jvmClass.isLocalClass() ) return Optional.empty();
        if( jvmClass.isEnum() ) return Optional.empty();
        if( jvmClass.isArray() ) return Optional.empty();
        if( jvmClass.isPrimitive() ) return Optional.empty();

        List<JvmObjMember> members = new ArrayList<>();
        Set<JvmObjMember> recursive = new HashSet<>();

        // fields
        for( var meth : jvmClass.getMethods() ){
            if( meth.getParameterCount() > 0 ) continue;
            if( meth.getReturnType() == Void.class ) continue;
            if( meth.getReturnType() == void.class ) continue;
            /////////////
            var rt = meth.getGenericReturnType();
            if( rt.equals(jvmType) ){
                // recursive
                var fld = new JvmObjField(meth.getName(), JvmType.THIS);
                members.add(fld);
                recursive.add(fld);
                continue;
            }
            var jt = JvmType.of(rt);
            if( jt.isEmpty() ) continue;

            var fld = new JvmObjField(meth.getName(), jt.get());
            members.add(fld);
        }

        var jvmObj = new JvmObjNamed(jvmClass.getName(), members);

        // change recursive
        for( int mi = 0; mi < members.size(); mi++ ){
            var m = members.get(mi);
            if( recursive.contains(m) ){
                if( m instanceof JvmObjField ){
                    var f = (JvmObjField) m;
                    f = new JvmObjField(f.name(), jvmObj);
                    members.set(mi, f);
                }
            }
        }

        return Optional.of(jvmObj);
    }

    @Override
    public String toString(){
        return toString0();
    }

    private final ThreadLocal<Integer> toStringCall = new ThreadLocal<>() {
        @Override
        protected Integer initialValue(){
            return 0;
        }
    };

    private String toString0(){
        var level = toStringCall.get();
        try{
            toStringCall.set(level + 1);
            if( level > 0 ){
                if( this instanceof Named ){
                    return "";
                } else {
                    return "!recursion";
                }
            }

            StringBuilder sb = new StringBuilder();
            sb.append("{").append(System.lineSeparator());

            StringBuilder sm = new StringBuilder();
            for( var m : members ){
                if( sm.length() > 0 ) sm.append(System.lineSeparator());
                sm.append(m.toString());
            }
            sb.append(Text.indent("  ", sm.toString()));

            sb.append(System.lineSeparator());
            sb.append("}").append(System.lineSeparator());
            return sb.toString();
        } finally {
            toStringCall.set(level);
        }
    }
}

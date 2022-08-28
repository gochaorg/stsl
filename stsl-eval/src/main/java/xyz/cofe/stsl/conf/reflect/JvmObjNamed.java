package xyz.cofe.stsl.conf.reflect;

import xyz.cofe.stsl.types.TAnon;
import xyz.cofe.stsl.types.TObject;

import java.util.List;

public class JvmObjNamed extends JvmObjContainer implements Named, ToStslType {
    public final String name;

    public JvmObjNamed( String name, List<JvmObjMember> members ){
        super(members);

        if( name == null ) throw new IllegalArgumentException("name==null");
        this.name = name;
    }

    @Override
    public String name(){
        return name;
    }

    @Override
    public String toString(){
        return name + " " + super.toString();
    }

    public xyz.cofe.stsl.types.Type toStslType(){
        var self = this;
        var tobj = TObject.create(name).fileds(fb -> {
            members.stream().filter(f -> f instanceof JvmObjField).map(f -> (JvmObjField) f).forEach(jvmField -> {
                if( jvmField.type() == self ){
                    fb.field(jvmField.name(), xyz.cofe.stsl.types.Type.THIS()).add();
                } else {
                    if( jvmField.type() instanceof JvmPrimitive ){
                        var jvmPrim = (JvmPrimitive) jvmField.type();
                        fb.field(jvmField.name(), jvmPrim.stslType).add();
                    } else if( jvmField.type() instanceof JvmObjNamed ){
                        var jvmObj = (JvmObjNamed) jvmField.type();
                        fb.field(jvmField.name(), jvmObj.toStslType()).add();
                    } else {
                        throw new UnsupportedOperationException("not impl for jvmType " + jvmField.type());
                    }
                }
            });
        }).build();
        return TAnon.from(tobj);
    }
}

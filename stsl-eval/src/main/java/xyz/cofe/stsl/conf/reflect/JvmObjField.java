package xyz.cofe.stsl.conf.reflect;

public class JvmObjField implements JvmObjMember {
    private final String name;
    private final JvmType type;

    public JvmObjField( String name, JvmType type ){
        this.name = name;
        this.type = type;
    }

    public JvmType type(){
        return type;
    }

    @Override
    public String name(){
        return name;
    }

    @Override
    public String toString(){
        return name + " : " + type;
    }
}

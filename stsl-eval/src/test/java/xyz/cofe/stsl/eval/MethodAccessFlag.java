package xyz.cofe.stsl.eval;

import xyz.cofe.jvmbc.cls.MethodFlags;

public class MethodAccessFlag implements MethodFlags {
    public MethodAccessFlag(){}
    public MethodAccessFlag(int access){
        this.access = access;
    }

    private int access;

    public int getAccess(){
        return access;
    }
    public void setAccess( int access ){
        this.access = access;
    }
}

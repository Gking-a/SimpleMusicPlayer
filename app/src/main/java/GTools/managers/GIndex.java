package gtools.managers;

public class GIndex {
    public GIndex(Object o) {
        if (o != null) {
            clazz=o.getClass();
            Verification = o.toString();
        }
    }
    private Class clazz=null;
    private String Verification=null;
    public String getVerification(){
        return Verification;
    }
    public Class getClazz() {
        return clazz;
    }
    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof GIndex)){
            return false;
        }else {
            GIndex o=(GIndex) obj;
            String v2=o.getVerification();
            Class v3=o.getClazz();
            if(clazz==null&&v3==null)
                return true;
            if(!clazz.equals(v3))
                return false;
            if(Verification==null&&v2==null)
                return true;
            if(Verification!=null&&v2!=null){
                if(Verification.length()!=v2.length())
                    return false;
                boolean b=true;
                for(int i=0;i<Verification.length();i++){
                    if(Verification.charAt(i)!=v2.charAt(i)){
                        b=false;
                    }
                }
                return b;
            }
            return false;
        }
    }
}

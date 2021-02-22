package GTools;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GJson {
//    public static void p(Object o) {
//        System.out.println(o.toString());
//    }
    private String line;
    private ValueClass nc;
    private ValueClass n0;
    public GJson(InputStream inputStream){
        BufferedReader b=new BufferedReader(new InputStreamReader(inputStream));
        try {
            line=b.readLine();
            Log.i("",line);
            b.close();
            inputStream.close();
            m=p.matcher(line);
            m2=p2.matcher(line);
            m3 = p3.matcher(line);
            n0=new ValueClass(null);
            nc=n0;
             jiexi();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private final static Pattern p3=Pattern.compile("^\"(.*?)\":(.*)");
    private final static Pattern p4=Pattern.compile("^\"(.*?)\"");
    private final static Pattern p=Pattern.compile("^\\{(.*)");
    private final static Pattern p2=Pattern.compile("^\\[(.*)");
    private Matcher m;
    private Matcher m2;
    private Matcher m3 ;
    private Matcher m4 ;
//分解
    private void jiexi(){
        if(line.length()==0){
            return;
        }
        int i3 = line.indexOf("}");
        int i4 = line.indexOf("]");
        int i6=line.indexOf(",");
        int min1=getMin(i3,i4);
        if(min1==0){
            nc=nc.su;
            line=line.substring(1);
            jiexi();
            return;
        }
        if(i6==0){
            line=line.substring(1);
            jiexi();
            return;
        }
        m=p.matcher(line);
        m2=p2.matcher(line);
        m3 = p3.matcher(line);
        boolean finished=false;
        if(m.find()&&!finished){
            finished=true;
            ValueClass n=new ValueClass(nc);
            nc.ac.add(n);
            line=line.substring(1);
            nc=n;
            jiexi();
        }
        if(m2.find()&&!finished){
            finished=true;
            ValueClass n=new ValueClass(nc);
            line=line.substring(1);
            nc.ac.add(n);
            nc=n;
            jiexi();
        }
        if (m3.find()&&!finished) {
            String k=m3.group(1);
            String v1=m3.group(2);
            m=p.matcher(v1);
            m2 = p2.matcher(v1);
            if(k.contains("\",")||k.contains("\"]")||k.contains("\"}")||(k.contains("\"")&&!k.contains("\\\""))){
                line=line.substring(k.indexOf("\"",1)+2);
                jiexi();
                return;
            }
            if(m.find()||m2.find()){
                ValueClass n=new ValueClass(nc);
                nc.put(k,n);
                nc=n;
                line=v1.substring(1);
            }else{
                int i1 = v1.indexOf("}");
                int i2 = v1.indexOf("]");
                int i5=v1.indexOf(",");
                m4 = p4.matcher(v1);
                if (m4.find()) {
                    //bug + 1 ;
                    i5=v1.indexOf("\",")+1;
                }
                int min=getMin(i1,i2,i5);
                String v2=v1.substring(0,min);
                if(min<0){
                    nc.put(k,v2);
                    jiexi();
                    return;
                }
                if(min==i1||min==i2){
                    line=v1.substring(min+1);
                    //bug + 2;
                    v2=v1.substring(0,min);
                    nc.put(k,v2);
                    nc=nc.su;
                }else {
                    m4 = p4.matcher(v2);
                    if (m4.find()) {
                        v2 = v1.substring(1, min - 1);
                    }
                    line = v1.substring(min + 1);
                    nc.put(k, v2);
                }
            }
            jiexi();
        }
    }
    public class ValueClass {
        public ValueClass su;//father
        public Map<String,String> kv;//value
        public Map<String, ValueClass> kc;//sons
        public ArrayList<ValueClass> ac;//sons
        public ValueClass(ValueClass su){
            this.su=su;
            kv=new HashMap<>();
            kc=new HashMap<>();
            ac=new ArrayList<>();
        }
        public boolean isRoot(){
            if(su==null){
                return true;
            }
            return false;
        }
        public boolean hasSon(){
            if(kc.size()!=0||ac.size()!=0){
                return true;
            }
            return false;
        }
        public ValueClass getOnlySon(){
            return ac.get(0);
        }
        public void put(String k,String v){
            kv.put(k,v);
        }
        public void put(String k, ValueClass v){
            kc.put(k,v);
        }
        public void put(ValueClass v){
            ac.add(v);
        }
        public String getString(String key){
            return kv.get(key);
        }
        public ValueClass getSon(String key){
            return kc.get(key);
        }
        public ArrayList<String> searchSons(String key){
            ArrayList<String > list=new ArrayList<>();
            m1(this,list,key);
            return list;
        }
    }
    public void m1(ValueClass vcs,ArrayList<String> list,String key){
        if(vcs.kv.containsKey(key)){
            list.add(vcs.kv.get(key));
        }
        for(ValueClass vc:vcs.kc.values()){
            m1(vc,list,key);
        }
        for(ValueClass vc:vcs.ac){
            m1(vc,list,key);
        }
    }
    private int getMin(int... args){
        int min=2147483647;
        for(int i:args){
            if(i<min&&i>=0){
                min=i;
            }
        }
        return min;
    }
    public ValueClass getRoot(){
        return n0;
    }
}

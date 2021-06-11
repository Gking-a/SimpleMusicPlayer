package gtools;
import gtools.managers.GHolder;
import java.util.ArrayList;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import gtools.util.GTimer;
public class SingleThreadTask
{
    public void execute(GHolder args){
        
    }
    public SingleThreadTask newInstance(){
        Class clazz=this.getClass();
        Constructor c=clazz.getConstructors()[0];
        try {
            SingleThreadTask newInstance=(SingleThreadTask)c.newInstance();
            return newInstance;
        } catch (IllegalAccessException e) {} catch (InvocationTargetException e) {} catch (InstantiationException e) {} catch (IllegalArgumentException e) {}
        return null;
    }
    
    public static class ThreadManager{
        private static int ThreadLength=0;
        private static ArrayList<Proxy> queue=new ArrayList<>();
        private static ArrayList<RuntimeThread> pool=new ArrayList<>();
        public static RuntimeThread require(Proxy p,int outTime){
            queue.add(p);
            GTimer timer=new GTimer();
            do{
                if(pool.size()!=0){
                    if(p.equals(queue.get(0))){
                        queue.remove(0);
                        RuntimeThread r=pool.get(0);
                        pool.remove(0);
                        return r;
                    }
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
            }while(!timer.compareBigger(outTime));
            return null;
        }
        public static void setThreadLength(int length){
            if(ThreadLength<length){
                for(int i=0;i<length-ThreadLength;i++){
                    RuntimeThread rt=new RuntimeThread();
                    rt.start();
                    pool.add(rt);
                }
            }
            ThreadLength=length;
            if(ThreadLength<pool.size()){
                for(int i=0;i<ThreadLength-pool.size();i++){
                    pool.remove(i);
                }
            }
        }
        public static void back(RuntimeThread rt){
            if(ThreadLength>pool.size()){
                pool.add(rt);
            }else{
                rt.release();
            }
        }
    }
    
}

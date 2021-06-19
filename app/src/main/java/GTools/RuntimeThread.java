package gtools;
import gtools.managers.GHolder;
public class RuntimeThread extends Thread{
    Proxy proxy;
    private boolean isReleased=false;
    public void setProxy(Proxy p){
        this.proxy=p;
    }
    public Proxy getProxy(){
        return proxy;
    }
    public void release(){
        isReleased=true;
    }
    public void run(){
        while(true){
            if(isReleased){
                break;
            }
            if(proxy!=null){
                GHolder args;
                if(proxy.getQueue().size()!=0&&(args=proxy.getTask())!=null){
                    proxy.newInstance().execute(args);
                    if(proxy.finished()){
                        proxy=null;
                    }
                }
            }
            try {
                sleep(100);
            } catch (InterruptedException e) {}
        }
        stop();
    }
}

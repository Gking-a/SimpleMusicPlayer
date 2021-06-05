package gtools;
import gtools.managers.GHolder;
public class RuntimeThread extends Thread{
    Proxy proxy;
    private boolean isReleased=false;
    public void setPxory(Proxy p){
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
                if((args=proxy.getTask())!=null){
                    proxy.getInstance().execute(args);
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

package gtools.util;
import java.util.Date;

public class GTimer
{
    private long t1=0;
    public GTimer(){
        t1=System.currentTimeMillis();
    }
    public GTimer(long time){
        t1=time;
    }
    public GTimer(Date date){
        t1=date.getTime();
    }
    public long getRise(){
        return System.currentTimeMillis()-t1;
    }
    public boolean compareBigger(long t){
        return System.currentTimeMillis()-t1>t;
    }
    public void reset(){
        t1=System.currentTimeMillis();
    }
}

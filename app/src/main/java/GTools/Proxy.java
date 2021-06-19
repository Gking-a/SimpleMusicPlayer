package gtools;

import gtools.managers.GHolder;
import java.util.ArrayList;
import gtools.SingleThreadTask.ThreadManager;
public class Proxy {
    public Proxy(SingleThreadTask obj){
        target=obj;
    }
    private RuntimeThread runtimeThread=null;
    private SingleThreadTask target;
    public static final int RUNNING=0;
    public static final int WAITING=1;
    public static final int STOPPING=2;
    public static final int POLICY_FIFO=0;
    public static final int POLICY_KEEP_WITTING=0;
    public static final int POLICY_FINISH_STOPPING=1;
    private ArrayList<GHolder> queue=new ArrayList<>();
    private ArrayList<GHolder> priorityQueue=new ArrayList<>();
    private int State=2;
    private int Execute_Order=0;
    private int Task_Finished=1;
    public int getState(){
        return State;
    }
    public Proxy setFinishPolicy(int policy){
        Task_Finished=policy;
        return this;
    }
    public void addTask(GHolder args){
        queue.add(args);
    }
    public void addSpecialTask(GHolder args){
        priorityQueue.add(args);
    }
    public ArrayList<GHolder> getQueue(){
        return queue;
    }
    public SingleThreadTask newInstance(){
        return target.newInstance();
    }
    public SingleThreadTask getInstance(){
        return target;
    }
    public GHolder getTask(){
        if(Execute_Order==POLICY_FIFO){
            GHolder task=null;
            if(priorityQueue.size()!=0){
                task=priorityQueue.get(0);
                priorityQueue.remove(0);
            }
            if(queue.size()!=0){
                task=queue.get(0);
                queue.remove(0);
            }
            return task;
        }
        return null;
    }
    public boolean finished(){
        if(Task_Finished==POLICY_FINISH_STOPPING){
            ThreadManager.back(runtimeThread);
            return true;
        }
        if(Task_Finished==POLICY_KEEP_WITTING)
            return false;
        return true;
    }
    public boolean restart(){
        runtimeThread=SingleThreadTask.ThreadManager.require(this,3*1000);
        if(runtimeThread!=null){
            runtimeThread.setProxy(this);
            return true;
        }
        return false;
    }
}

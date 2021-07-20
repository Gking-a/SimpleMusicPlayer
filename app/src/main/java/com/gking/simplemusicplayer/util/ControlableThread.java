package com.gking.simplemusicplayer.util;

public class ControlableThread extends Thread{
    private final Object lock=new Object();
    private boolean suspend=false;
    Runnable runnable;
    boolean locked=false;
    public ControlableThread(Runnable runnable) {
        super();
        this.runnable = runnable;
    }
    @Override
    public void run() {
        while (!interrupted()){
            synchronized (lock){
                if(suspend) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            runnable.run();
        }
    }
    public void setSuspend(boolean suspend) {
        this.suspend = suspend;
        if(!suspend){
            synchronized (lock){
                lock.notifyAll();
            }
        }
    }
}

package com.gking.simplemusicplayer.util;

public class ControlableThread extends Thread{
    private final Object lock=new Object();
    private boolean suspend=false;
    Runnable runnable;
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
            try{
                runnable.run();
            }catch (Exception e){
                e.printStackTrace();
            }
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

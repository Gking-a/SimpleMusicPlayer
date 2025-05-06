//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.gking.gtools.util;

import java.util.Date;

public class GTimer {
    private long t1 = 0L;

    public GTimer() {
        this.t1 = System.currentTimeMillis();
    }

    public GTimer(long time) {
        this.t1 = time;
    }

    public GTimer(Date date) {
        this.t1 = date.getTime();
    }

    public long getRise() {
        return System.currentTimeMillis() - this.t1;
    }

    public boolean compareBigger(long t) {
        return System.currentTimeMillis() - this.t1 > t;
    }

    public void reset() {
        this.t1 = System.currentTimeMillis();
    }
}

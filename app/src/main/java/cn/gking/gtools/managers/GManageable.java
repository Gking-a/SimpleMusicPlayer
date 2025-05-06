//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.gking.gtools.managers;

public interface GManageable<Index, Content> {
    Content get(Index var1);

    void remove(Index var1);

    void add(Index var1, Content var2);

    void replace(Index var1, Content var2);
}

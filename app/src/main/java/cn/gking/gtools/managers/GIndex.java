//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package cn.gking.gtools.managers;

public class GIndex {
    private Class clazz = null;
    private String Verification = null;

    public GIndex(Object o) {
        if (o != null) {
            this.clazz = o.getClass();
            this.Verification = o.toString();
        }

    }

    public String getVerification() {
        return this.Verification;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof GIndex)) {
            return false;
        } else {
            GIndex o = (GIndex)obj;
            String v2 = o.getVerification();
            Class v3 = o.getClazz();
            if (this.clazz == null && v3 == null) {
                return true;
            } else if (!this.clazz.equals(v3)) {
                return false;
            } else if (this.Verification == null && v2 == null) {
                return true;
            } else if (this.Verification != null && v2 != null) {
                if (this.Verification.length() != v2.length()) {
                    return false;
                } else {
                    boolean b = true;

                    for(int i = 0; i < this.Verification.length(); ++i) {
                        if (this.Verification.charAt(i) != v2.charAt(i)) {
                            b = false;
                        }
                    }

                    return b;
                }
            } else {
                return false;
            }
        }
    }
}

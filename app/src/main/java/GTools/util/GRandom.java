package gtools.util;

import java.util.Random;

public class GRandom
{
    private static Random random=new Random();
    public static boolean nextBoolean(){return random.nextBoolean();}
    public static int nextInt(){return random.nextInt();}
    public static float nextFloat(){return random.nextFloat();}
    public static double nextDouble(){return random.nextDouble();}
    public static long nextLong(){return random.nextLong();}
    public static byte nextByte(){return (byte)fill(8);}
    public static byte nextBinary(){return (byte)(nextBoolean()?1:0);}
    public static char nextChar(){return (char) fill(16);}
    public static short nextShort(){return (short) fill(16);}
    public static long fill(int length){
        long a=0;
        for (int i = 0; i < length; i++) {
            a=a*2+nextBinary();
        }
        return a;
    }
}

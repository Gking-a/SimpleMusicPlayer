
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class Util  
{
    private static String IP=null;
    private static final Pattern p=Pattern.compile("<h4 class=\"mb5 tl z-tl\">您的IP地址：<span class=\"c-red\">(.*?)</span></h4>");
    public static String getIP() throws MalformedURLException, IOException{
        if(IP!=null)
            return IP;
        URL u=new URL("http://mip.chinaz.com/");
        BufferedReader br=new BufferedReader(new InputStreamReader(u.openStream()));
        String line;
        Matcher matcher;
        while((line=br.readLine())!=null){
            matcher=p.matcher(line);
            if(matcher.find()){
                IP=matcher.group(1);
                return matcher.group(1);
            }
        }
        return null;
    }
    public static long ByteToType(byte[] decode){
        if(decode.length==8){
            return decode[0]<<7*8+decode[1]<<6*8+decode[2]<<5*8+decode[3]<<4*8+
                    decode[4]<<3*8+decode[5]<<2*8+decode[6]<<1*8+decode[7]<<0*8;
        }else if(decode.length==4){
            return decode[0]<<3*8+decode[1]<<2*8+decode[2]<<1*8+decode[3]<<0*8;
        }else if(decode.length==2){
            return decode[0]<<8+decode[1];
        }else if(decode.length==1){
            return decode[0];
        }
        return 0;
    }
    //b[0]为最高位
    public static byte[] TypeToBytes(Object o){
        if(o instanceof Byte){
            return new byte[]{(byte)o};
        }else if(o instanceof Character||o instanceof Short){
            return new byte[]{(byte)(((short)o>>8)&0xff),(byte)((short)o&0xff)};
        }else if(o instanceof Integer){
            return new byte[]{(byte)(((int)o>>24)&0xff),(byte)(((int)o>>16)&0xff),
                (byte)(((int)o>>8)&0xff),(byte)(((int)o>>0)&0xff)};
        }else if(o instanceof Long){
            return new byte[]{(byte)(((long)o>>7*8)&0xff),(byte)(((long)o>>6*8)&0xff),
                (byte)(((long)o>>5*8)&0xff),(byte)(((long)o>>4*8)&0xff),(byte)(((long)o>>3*8)&0xff),
                (byte)(((long)o>>2*8)&0xff),(byte)(((long)o>>1*8)&0xff),(byte)(((long)o>>0*8)&0xff)};
        }
        return null;
    }
    public static byte[] BytesWithBytes(byte[] ...bytes){
        int length=0;
        for(byte[] b:bytes){
            length+=b.length;
        }
        byte[] data=new byte[length];
        int p=0;
        for(byte[] b:bytes){
            for(int i=0;i<b.length;i++){
                data[p]=b[i];
                p++;
            }
        }
        return data;
    }
    private static final Pattern p2=Pattern.compile("(.*?)\\.(.*?)\\.(.*?)\\.(.*)");
    public static byte[] getByteIP(String ip){
        Matcher m=p2.matcher(ip);
        if(m.find()){
            System.out.println(ip);
            byte a=(byte)(Integer.valueOf(m.group(1))&0xff),
                b=(byte)(Integer.valueOf(m.group(2))&0xff),
                c=(byte)(Integer.valueOf(m.group(3))&0xff),
                d=(byte)(Integer.valueOf(m.group(4))&0xff);
                return new byte[]{a,b,c,d};
        }
        return null;
    }
    public static int getNumBeforePoint(double num){
        String a=num+"";
        if(!a.contains("."))
            return (int)num;
        return Integer.parseInt(a.substring(0,a.indexOf(".")));
    }
}

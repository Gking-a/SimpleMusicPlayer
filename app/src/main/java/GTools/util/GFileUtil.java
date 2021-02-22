package GTools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class GFileUtil {
    private GFileUtil(){}
    public static boolean CopyFile(String source,String target){
        return CopyFile(new File(source),new File(target));
    }
    public static boolean CopyFile(String source,File target){
        return CopyFile(new File(source),target);
    }
    public static boolean CopyFile(File source,String target){
        return CopyFile(source,new File(target));
    }
    public static boolean CopyFile(File source,File target){
        boolean result=false;
        try {
            byte[] buffer=new byte[1024];
            FileInputStream inputStream=new FileInputStream(source);
            FileOutputStream outputStream=new FileOutputStream(target);
            while (inputStream.read(buffer)>0){
                outputStream.write(buffer);
            }
            inputStream.close();
            outputStream.flush();
            outputStream.close();
            result=true;
        }catch (IOException e){}
        return result;
    }
}

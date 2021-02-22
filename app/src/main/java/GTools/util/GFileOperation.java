package GTools.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public final class GFileOperation{
    private GFileOperation(){}
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

package com.gkingswq.simplemusicplayer.util;
import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.BadPaddingException;
import java.io.UnsupportedEncodingException;

public class Require {
    public static String encrypt(String parem,String key){
        try {
            Cipher cipher=Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec secretKey=new SecretKeySpec(key.getBytes(),"AES");
            IvParameterSpec iv=new IvParameterSpec("0102030405060708".getBytes());
            cipher.init(Cipher.ENCRYPT_MODE,secretKey,iv);
            byte[] b=cipher.doFinal(parem.getBytes("utf-8"));
        } catch (NoSuchPaddingException e) {} 
        catch (NoSuchAlgorithmException e) {}
        catch (InvalidAlgorithmParameterException e){}
        catch (InvalidKeyException e){}
        catch (IllegalBlockSizeException e){}
        catch (BadPaddingException e){}
        catch (UnsupportedEncodingException e){}
    }
    
    
}

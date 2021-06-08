/*
 */

package com.gkingswq.simplemusicplayer.util;
import java.util.HashMap;
import java.util.List;
import java.math.BigInteger;
import java.util.Collections;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
public class Web163 {
    private static final String modulus = "00e0b509f6259df8642dbc35662901477df22677ec152b5ff68ace615bb7b725152b3ab17a876aea8a5aa76d2e417629ec4ee341f56135fccf695280104e0312ecbda92557c93870114af6c9d05c4f7f0c3685b7a46bee255932575cce10b424d813cfe4875d3e82047b97ddef52741d546b8e289dc6935b3ece0462db0a22b8e7";
    private static final String nonce = "0CoJUm6Qyw8W8jud";
    private static final String pubKey = "010001";

    /**
     * 创建秘钥
     * @param size  位数
     * @return
     */
    public static String createSecreKey(int size){
        String keys = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        String key = "";
        for(int i = 0;i<size;i++){
            int pos = (int)Math.floor(Math.random()*keys.length());
            key = key+String.valueOf(keys.charAt(pos));
        }
        return key;
    }
    /**
     * 字符串转换成十六进制字符串
     */
    public static String str2HexStr(String str) {

        char[] chars = "0123456789ABCDEF".toCharArray();
        StringBuilder sb = new StringBuilder("");
        byte[] bs = str.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(chars[bit]);
            bit = bs[i] & 0x0f;
            sb.append(chars[bit]);
        }
        return sb.toString();
    }

    /**
     * aes加密
     * @param sSrc
     * @param sKey
     * @return
     * @throws Exception
     */
    public static String aesEncrypt(String sSrc, String sKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] raw = sKey.getBytes();
        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
        IvParameterSpec iv = new IvParameterSpec("0102030405060708".getBytes());//使用CBC模式，需要一个向量iv，可增加加密算法的强度
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);
        byte[] encrypted = cipher.doFinal(sSrc.getBytes("utf-8"));
        return new Base64().encodeToString(encrypted);//此处使用BASE64做转码。
    }
    /**
     * 截取长度
     * @param str
     * @param size
     * @return
     */
    public static String zfill(String str,int size){
        while(str.length()<size) {
            str = "0" + str;
        }
        return str;
    }

    /**
     * 16进制字符串转字节数组
     * @param hexString
     * @return
     */
    public static byte[] hexToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }

        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] bytes = new byte[length];
        String hexDigits = "0123456789abcdef";
        for (int i = 0; i < length; i++) {
            int pos = i * 2; // 两个字符对应一个byte
            int h = hexDigits.indexOf(hexChars[pos]) << 4; // 注1
            int l = hexDigits.indexOf(hexChars[pos + 1]); // 注2
            if(h == -1 || l == -1) { // 非16进制字符
                return null;
            }
            bytes[i] = (byte) (h | l);
        }
        return bytes;
    }

    /**
     * rsa加密算法
     * @param text
     * @param pubKey
     * @param modulus
     * @return
     */
    public static String rsaEncrypt(String text,String pubKey,String modulus){
        List<String> list = Arrays.asList(text.split(""));
        Collections.reverse(list);
        String _text = StringUtils.join(list,"");
        BigInteger biText = new BigInteger(1,_text.getBytes());
        BigInteger biEx = new BigInteger(1,hexToBytes(pubKey));
        BigInteger biMod = new BigInteger(1,hexToBytes(modulus));
        String biRet = biText.modPow(biEx,biMod).toString(16);
        return zfill(biRet,256);
    }

    /**
     * 网易云音乐参数加密
     * @param paras
     * @return
     * @throws Exception
     */
    public static HashMap<String,String> encrypt(String paras) throws Exception {
        System.err.println(paras);
        String secKey = createSecreKey(16);
        String encText = aesEncrypt(aesEncrypt(paras,nonce),secKey);
        String encSecKey = rsaEncrypt(secKey,pubKey,modulus);
        HashMap<String,String> datas = new HashMap<>();
        datas.put("params",encText);
        datas.put("encSecKey",encSecKey);
        return datas;
    }
    
}


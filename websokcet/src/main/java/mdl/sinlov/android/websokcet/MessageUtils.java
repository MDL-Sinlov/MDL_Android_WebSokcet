package mdl.sinlov.android.websokcet;

import java.io.UnsupportedEncodingException;

/**
 * <pre>
 *     sinlov
 *
 *     /\__/\
 *    /`    '\
 *  ≈≈≈ 0  0 ≈≈≈ Hello world!
 *    \  --  /
 *   /        \
 *  /          \
 * |            |
 *  \  ||  ||  /
 *   \_oo__oo_/≡≡≡≡≡≡≡≡o
 *
 * </pre>
 * Created by "sinlov" on 16/8/26.
 */
public class MessageUtils {

    public static String byteArray2String(byte[] bytes) {
        return byteArray2String(bytes, "UTF-8");
    }

    public static String byteArray2String(byte[] bytes, String charsetName) {
        String res = "";
        try {
            res = new String(bytes, charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }

    public static byte[] string2ByteArray(String string) {
        return string.getBytes();
    }

    public static byte[] string2ByteArray(String string, String charsetName) {
        try {
            return string.getBytes(charsetName);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}

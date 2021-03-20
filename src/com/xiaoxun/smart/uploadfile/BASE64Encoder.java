package com.xiaoxun.smart.uploadfile;

import java.io.UnsupportedEncodingException;

/**
 * Created by guxiaolong on 2017/8/3.
 */

public class BASE64Encoder {
    private static final char LAST2BYTE = (char) Integer.parseInt("00000011", 2);
    private static final char LAST4BYTE = (char) Integer.parseInt("00001111", 2);
    private static final char LAST6BYTE = (char) Integer.parseInt("00111111", 2);
    private static final char LEAD6BYTE = (char) Integer.parseInt("11111100", 2);
    private static final char LEAD4BYTE = (char) Integer.parseInt("11110000", 2);
    private static final char LEAD2BYTE = (char) Integer.parseInt("11000000", 2);
    private static final char[] ENCODE_TABLE = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '+', '/'};
    private static final String DEFAULT_ENCODING = "UTF-8";

    public BASE64Encoder() {
    }

    public static String encode(String data) throws UnsupportedEncodingException {
        return encode(data.getBytes("UTF-8"));
    }

    public static String encode(byte[] from) {
        StringBuffer to = new StringBuffer((int)((double)from.length * 1.34D) + 3);
        int num = 0;
        char currentByte = 0;

        int i;
        for(i = 0; i < from.length; ++i) {
            for(num %= 8; num < 8; num += 6) {
                switch(num) {
                    case 0:
                        currentByte = (char)(from[i] & LEAD6BYTE);
                        currentByte = (char)(currentByte >>> 2);
                        break;
                    case 1:
                    case 3:
                    case 5:
                    default:
                        break;
                    case 2:
                        currentByte = (char)(from[i] & LAST6BYTE);
                        break;
                    case 4:
                        currentByte = (char)(from[i] & LAST4BYTE);
                        currentByte = (char)(currentByte << 2);
                        if(i + 1 < from.length) {
                            currentByte = (char)(currentByte | (from[i + 1] & LEAD2BYTE) >>> 6);
                        }
                        break;
                    case 6:
                        currentByte = (char)(from[i] & LAST2BYTE);
                        currentByte = (char)(currentByte << 4);
                        if(i + 1 < from.length) {
                            currentByte = (char)(currentByte | (from[i + 1] & LEAD4BYTE) >>> 4);
                        }
                }

                to.append(ENCODE_TABLE[currentByte]);
            }
        }

        if(to.length() % 4 != 0) {
            for(i = 4 - to.length() % 4; i > 0; --i) {
                to.append("=");
            }
        }

        return to.toString();
    }
}

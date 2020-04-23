package cn.nubia.gamelauncher.util;

public class ByteUtil {
    private static char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String byteArray2hexString(byte[] data) {
        StringBuilder sb = new StringBuilder();
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                sb.append(HEX_CHAR[(data[i] & 240) >>> 4]);
                sb.append(HEX_CHAR[data[i] & 15]);
                if (i < data.length - 1) {
                    sb.append(" ");
                }
            }
        }
        return sb.toString();
    }
}

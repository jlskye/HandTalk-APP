package animation;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * 工具类
 * */
public class Util {

    public static FloatBuffer floatToBuffer(float[] a){
        ByteBuffer bb = ByteBuffer.allocateDirect(a.length * Float.SIZE);
        bb.order(ByteOrder.nativeOrder());
        FloatBuffer buffer = bb.asFloatBuffer();
        buffer.put(a);
        buffer.position(0);
        return buffer;
    }

    public static int byte4ToInt(byte[] bytes, int offset){
        int b3 = bytes[offset + 3] & 0xFF;
        int b2 = bytes[offset + 2] & 0xFF;
        int b1 = bytes[offset + 1] & 0xFF;
        int b0 = bytes[offset] & 0xFF;
        int ret = (b3 << 24) | (b2 << 16) | (b1 << 8) | (b0);
        return ret;
    }

    public static short byte2ToShort(byte[] bytes, int offset){
        int b1 = bytes[offset + 1] & 0xFF;
        int b0 = bytes[offset] & 0xFF;
        short ret = (short) ((b1 << 8) | b0);
        return ret;
    }

    public static float byte4ToFloat(byte[] bytes, int offset){
        return Float.intBitsToFloat(byte4ToInt(bytes, offset));
    }
    public static int readInt(InputStream is) throws IOException{
        byte[] bytes = new byte[4];
        is.read(bytes);
        int ret = byte4ToInt(bytes, 0);
        return ret;
    }
    public static float readFloat(InputStream is) throws IOException{
        byte[] bytes = new byte[4];
        is.read(bytes);
        float ret = byte4ToFloat(bytes, 0);
        return ret;
    }
    public static byte[] float2byte(float f) {

        // 把float转换为byte[]
        int fbit = Float.floatToIntBits(f);

        return int2byte(fbit);
    }

    public static byte[] int2byte(int n) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            b[i] = (byte) (n >> (24 - i * 8));
        }

        // 翻转数组
        int len = b.length;
        // 建立一个与源数组元素类型相同的数组
        byte[] dest = new byte[len];
        // 为了防止修改源数组，将源数组拷贝一份副本
        System.arraycopy(b, 0, dest, 0, len);
        byte temp;
        // 将顺位第i个与倒数第i个交换
        for (int i = 0; i < len / 2; ++i) {
            temp = dest[i];
            dest[i] = dest[len - i - 1];
            dest[len - i - 1] = temp;
        }
        return dest;
    }
}


